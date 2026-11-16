package app.seb3thehacker.gearslip.car

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.car.app.CarToast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import app.seb3thehacker.gearslip.GearslipLog
import app.seb3thehacker.gearslip.PermissionActivity
import app.seb3thehacker.gearslip.R
import app.seb3thehacker.gearslip.call.CallUiState
import app.seb3thehacker.gearslip.call.CarCalls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** What a contact tile's pin and moved place remember it by: the id of its phone-number row. */
internal const val CONTACT_PREFIX = "contact:"

/**
 * One number picked on the phone to call from a tile. Name, number and [type] ("Mobile") are a
 * copy taken when it was picked, so the tile still calls without READ_CONTACTS.
 */
internal data class ContactTile(
    /** [Phone._ID]: one number of one contact, so a tile calls the number it was given. */
    val dataId: Long,
    val lookupKey: String,
    val name: String,
    val number: String,
    val type: String,
) {
    val id: String get() = CONTACT_PREFIX + dataId

    companion object {
        // Control characters, since a name or a custom type label can hold any printable one.
        private const val FIELD = '\u001F'
        private const val RECORD = '\u001E'

        fun encode(tiles: List<ContactTile>): String = tiles.joinToString(RECORD.toString()) {
            listOf(it.dataId.toString(), it.lookupKey, it.name, it.number, it.type).joinToString(FIELD.toString()) { f -> f.filter { c -> c != FIELD && c != RECORD } }
        }

        fun decode(saved: String): List<ContactTile> = saved.split(RECORD).mapNotNull { record ->
            val f = record.split(FIELD)
            if (f.size != 5) return@mapNotNull null
            ContactTile(f[0].toLongOrNull() ?: return@mapNotNull null, f[1], f[2], f[3], f[4])
        }
    }
}

/**
 * The contact tiles as launcher entries, with their photos. Built by [refresh] at the start of a
 * car session and whenever the list changes on the phone; photos are kept in memory only.
 */
internal object ContactTiles {

    private val flow = MutableStateFlow<List<Entry>>(emptyList())
    val state: StateFlow<List<Entry>> = flow.asStateFlow()

    private sealed interface Lookup {
        class Found(val tile: ContactTile, val photo: ImageBitmap?) : Lookup
        /** The contacts say this number no longer exists - or, under Contact Scopes, that it is out of reach. */
        data object Gone : Lookup
    }

    private fun canRead(context: Context) =
        context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

    /**
     * Rereads each tile's contact: the name, number and photo as they are now. With [prune], a
     * tile whose number is gone is removed; without READ_CONTACTS nothing is, since there is no
     * telling whether it still exists.
     */
    @Synchronized
    fun refresh(context: Context, prune: Boolean = true) {
        val app = context.applicationContext
        if (!LauncherMode.canCall(app)) {
            flow.value = emptyList()
            return
        }
        val saved = CarSettings.contactTiles.value
        val readable = canRead(app)
        val photos = mutableMapOf<String, ImageBitmap?>()
        val kept = saved.mapNotNull { tile ->
            when (val found = if (readable) lookUp(app, tile) else null) {
                null -> tile
                Lookup.Gone -> tile.takeUnless { prune }
                is Lookup.Found -> found.tile.also { photos[it.id] = found.photo }
            }
        }
        // Only if the phone didn't change the list while this was reading.
        if (kept != saved && CarSettings.contactTiles.value == saved) {
            GearslipLog.i("contact tiles: ${saved.size - kept.size} removed, ${kept.size} kept")
            CarSettings.setContactTiles(kept)
        }
        flow.value = entriesFor(kept, photos)
    }

    /** False only when the contacts can be read and this one isn't among them, as with Contact Scopes. */
    fun isVisible(context: Context, tile: ContactTile): Boolean =
        !canRead(context) || lookUp(context, tile) != Lookup.Gone

    /** The number chosen in Android's picker; readable through the picker's grant, without READ_CONTACTS. */
    fun readPicked(context: Context, uri: Uri): ContactTile? = runCatching {
        context.contentResolver.query(
            uri, arrayOf(Phone._ID, Phone.LOOKUP_KEY, Phone.DISPLAY_NAME, Phone.NUMBER, Phone.TYPE, Phone.LABEL), null, null, null,
        )?.use { c ->
            if (!c.moveToFirst()) return@use null
            val number = c.getString(3)?.takeIf { it.isNotBlank() } ?: return@use null
            ContactTile(c.getLong(0), c.getString(1).orEmpty(), c.getString(2) ?: number, number, typeLabel(context, c.getInt(4), c.getString(5)))
        }
    }.onFailure { GearslipLog.w("contact tiles: could not read the picked number: ${it.message}") }.getOrNull()

    /** null when the contacts can't answer (no provider, refused), which isn't the same as gone. */
    private fun lookUp(context: Context, tile: ContactTile): Lookup? = runCatching {
        context.contentResolver.query(
            Phone.CONTENT_URI,
            arrayOf(Phone.CONTACT_ID, Phone.LOOKUP_KEY, Phone.DISPLAY_NAME, Phone.NUMBER, Phone.TYPE, Phone.LABEL),
            "${Phone._ID} = ?", arrayOf(tile.dataId.toString()), null,
        )?.use { c ->
            if (!c.moveToFirst()) return@use Lookup.Gone
            val now = tile.copy(
                lookupKey = c.getString(1) ?: tile.lookupKey,
                name = c.getString(2) ?: tile.name,
                number = c.getString(3)?.takeIf { it.isNotBlank() } ?: tile.number,
                type = typeLabel(context, c.getInt(4), c.getString(5)),
            )
            Lookup.Found(now, photo(context, c.getLong(0)))
        }
    }.getOrNull()

    private fun photo(context: Context, contactId: Long): ImageBitmap? = runCatching {
        val uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
        ContactsContract.Contacts.openContactPhotoInputStream(context.contentResolver, uri)
            ?.use { BitmapFactory.decodeStream(it) }?.asImageBitmap()
    }.getOrNull()

    private fun typeLabel(context: Context, type: Int, label: String?) =
        Phone.getTypeLabel(context.resources, type, label).toString()

    /** Two numbers of one person are told apart by type: "Maman · Travail". */
    private fun entriesFor(tiles: List<ContactTile>, photos: Map<String, ImageBitmap?>): List<Entry> {
        fun ContactTile.person() = lookupKey.ifEmpty { id }
        val twice = tiles.groupingBy { it.person() }.eachCount().filterValues { it > 1 }.keys
        return tiles.map { Entry(if (it.person() in twice) "${it.name} · ${it.type}" else it.name, photos[it.id], contact = it) }
    }
}

/**
 * What tapping a contact tile does: call at once. Not while another call is under way, and
 * without CALL_PHONE it asks on the phone instead, leaving the driver to tap again.
 */
internal fun callContact(context: Context, tile: ContactTile) {
    when {
        CarCalls.state.value is CallUiState.Active ->
            CarToasts.show(ContactTiles, context.getString(R.string.contact_call_busy), CarToast.LENGTH_SHORT)
        context.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED -> {
            CarToasts.show(ContactTiles, context.getString(R.string.contact_call_permission), CarToast.LENGTH_LONG)
            PermissionActivity.ask(context, arrayOf(Manifest.permission.CALL_PHONE))
        }
        else -> placeCall(context, tile.number)
    }
}

/** A contact's photo in a circle, or the first letter of their name when there is none to show. */
@Composable
internal fun ContactAvatar(name: String, photo: ImageBitmap?, size: Dp, modifier: Modifier = Modifier) {
    if (photo != null) {
        Image(photo, contentDescription = null, modifier.size(size).clip(CircleShape), contentScale = ContentScale.Crop)
        return
    }
    Box(modifier.size(size).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
        Text(
            name.trim().take(1).uppercase(),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.45f).sp,
        )
    }
}
