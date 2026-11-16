package app.seb3thehacker.gearslip.car

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ContactTilesTest {
    private val context = InMemoryContext()

    private fun tile(id: Long, name: String) = ContactTile(id, "key$id", name, "+3360000000$id", "Mobile")
    private fun entry(t: ContactTile) = Entry(t.name, icon = null, contact = t)

    private val zoe = tile(1, "Zoé")
    private val alice = tile(2, "Alice")

    @Before
    fun startWithNothingSaved() = CarSettings.init(context)

    @Test
    fun contactsCloseThePhoneGroupInTheirOwnOrder() {
        val all = BuiltInApps.everyEntry + listOf(entry(zoe), entry(alice))
        val order = launcherOrder(all, false, emptyList(), emptySet())
        val phone = order.indexOfFirst { it.builtIn === BuiltInApps.Phone }
        // Right after the Phone app, Zoé before Alice as given, not alphabetically.
        assertEquals(listOf(zoe, alice), order.subList(phone + 1, phone + 3).map { it.contact })
    }

    @Test
    fun theListSurvivesARestart() {
        val odd = ContactTile(3, "k,3", "Dupont, Jean", "+33 6 12", "Travail · bureau")
        CarSettings.setContactTiles(listOf(zoe, odd))
        CarSettings.init(context)
        assertEquals(listOf(zoe, odd), CarSettings.contactTiles.value)
    }

    @Test
    fun noMoreThanTheMaximumIsKept() {
        CarSettings.setContactTiles((1L..12L).map { tile(it, "n$it") })
        assertEquals(CarSettings.MAX_CONTACT_TILES, CarSettings.contactTiles.value.size)
    }

    @Test
    fun removingATileUnpinsItAndDropsItsMovedPlace() {
        CarSettings.setContactTiles(listOf(zoe, alice))
        CarSettings.togglePin(zoe.id)
        CarSettings.setAppOrder(listOf(alice.id, zoe.id))
        CarSettings.setContactTiles(listOf(alice))
        assertEquals(emptySet<String>(), CarSettings.pinnedApps.value)
        assertEquals(listOf(alice.id), CarSettings.appOrder.value)
    }
}
