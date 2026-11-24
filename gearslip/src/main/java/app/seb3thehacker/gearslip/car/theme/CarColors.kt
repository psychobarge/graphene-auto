package app.seb3thehacker.gearslip.car.theme

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import app.seb3thehacker.gearslip.car.AppColours
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp

/** The phone's Material You colours, or for [AppColours.GRAPHENE] the app's own, light or dark. */
fun baseColorScheme(context: Context, colours: AppColours, dark: Boolean): ColorScheme = when {
    colours == AppColours.GRAPHENE -> if (dark) GrapheneDark else GrapheneLight
    dark -> dynamicDarkColorScheme(context)
    else -> dynamicLightColorScheme(context)
}

/**
 * The launcher icon's palette in light: cool greys, with the cyan taken down to a teal that reads
 * on white, and the bright cyan kept for containers.
 */
private val GrapheneLight = lightColorScheme(
    primary = Color(0xFF006877),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA2EEFF),
    onPrimaryContainer = Color(0xFF001F25),
    inversePrimary = Color(0xFF22D3EE),
    secondary = Color(0xFF4A6267),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCDE7EC),
    onSecondaryContainer = Color(0xFF051F23),
    tertiary = Color(0xFF525E7D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDAE2FF),
    onTertiaryContainer = Color(0xFF0E1B37),
    background = Color(0xFFF5F8F9),
    onBackground = Color(0xFF171C1F),
    surface = Color(0xFFF5F8F9),
    onSurface = Color(0xFF171C1F),
    surfaceVariant = Color(0xFFDBE4E6),
    onSurfaceVariant = Color(0xFF3F484A),
    surfaceDim = Color(0xFFD5DBDD),
    surfaceBright = Color(0xFFF5F8F9),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEFF2F3),
    surfaceContainer = Color(0xFFE9EEEF),
    surfaceContainerHigh = Color(0xFFE3E8E9),
    surfaceContainerHighest = Color(0xFFDDE3E4),
    outline = Color(0xFF6F797B),
    outlineVariant = Color(0xFFBFC8CA),
    inverseSurface = Color(0xFF2C3134),
    inverseOnSurface = Color(0xFFEDF1F3),
)

/** The launcher icon's palette in dark: cyan #22D3EE on graphite #15191E. */
private val GrapheneDark = darkColorScheme(
    primary = Color(0xFF22D3EE),
    onPrimary = Color(0xFF00363F),
    primaryContainer = Color(0xFF004F5B),
    onPrimaryContainer = Color(0xFFA2EEFF),
    inversePrimary = Color(0xFF006877),
    secondary = Color(0xFFB1CBD0),
    onSecondary = Color(0xFF1C3438),
    secondaryContainer = Color(0xFF2C3A3F),
    onSecondaryContainer = Color(0xFFCDE7EC),
    tertiary = Color(0xFFBBC6EA),
    onTertiary = Color(0xFF253048),
    tertiaryContainer = Color(0xFF3B4760),
    onTertiaryContainer = Color(0xFFDBE2FF),
    background = Color(0xFF15191E),
    onBackground = Color(0xFFE1E3E6),
    surface = Color(0xFF15191E),
    onSurface = Color(0xFFE1E3E6),
    surfaceVariant = Color(0xFF3A4248),
    onSurfaceVariant = Color(0xFFBFC8CC),
    surfaceDim = Color(0xFF15191E),
    surfaceBright = Color(0xFF353B42),
    surfaceContainerLowest = Color(0xFF0F1317),
    surfaceContainerLow = Color(0xFF1A1F24),
    surfaceContainer = Color(0xFF1E2329),
    surfaceContainerHigh = Color(0xFF262C32),
    surfaceContainerHighest = Color(0xFF30363D),
    outline = Color(0xFF899296),
    outlineVariant = Color(0xFF3F484C),
    inverseSurface = Color(0xFFE1E3E6),
    inverseOnSurface = Color(0xFF2E3135),
)

/**
 * The phone's Material You colours, with its accent washed over the surfaces. Material You's own
 * surfaces are nearly neutral, which reads as flat grey on a car screen; tinting them with the
 * phone's primary colour (purple on a purple phone) gives the whole UI the phone's hue, the way
 * Material 3's surface tint does, and it changes with the wallpaper like everything else.
 * Higher containers take more of the tint, so cards still stand off the background.
 */
fun ColorScheme.withTintedSurfaces(dark: Boolean, style: DarkStyle = DarkStyle.GREY): ColorScheme {
    if (dark && style != DarkStyle.GREY) return withDarkStyle(style)
    val base = if (dark) 0.03f else 0.02f
    fun tint(own: Color, step: Int) = primary.copy(alpha = base + step * (if (dark) 0.015f else 0.01f)).compositeOver(own)
    return copy(
        background = tint(background, 0),
        surface = tint(surface, 0),
        surfaceDim = tint(surfaceDim, 0),
        surfaceContainerLowest = tint(surfaceContainerLowest, 0),
        surfaceContainerLow = tint(surfaceContainerLow, 1),
        surfaceContainer = tint(surfaceContainer, 2),
        surfaceContainerHigh = tint(surfaceContainerHigh, 3),
        surfaceContainerHighest = tint(surfaceContainerHighest, 4),
        surfaceBright = tint(surfaceBright, 4),
        surfaceVariant = tint(surfaceVariant, 3),
    )
}

/**
 * How dark the car's dark mode goes. [GREY] is Material You's own dark, with the tint above;
 * the others are darker, and the two colour ones take their hue from the phone's accent.
 */
enum class DarkStyle { GREY, BLACK, DEEP_COLOUR, BLACK_COLOUR }

/**
 * The surfaces rebuilt at [style]'s brightness, from the background up to the highest container.
 * The levels are 0-255 greys; each surface keeps Material You's own slight hue at that level.
 */
private fun ColorScheme.withDarkStyle(style: DarkStyle): ColorScheme {
    val levels = when (style) {
        DarkStyle.BLACK -> listOf(0f, 12f, 17f, 25f, 34f)
        DarkStyle.DEEP_COLOUR -> listOf(11f, 19f, 24f, 32f, 41f)
        else -> listOf(0f, 17f, 22f, 30f, 39f)
    }
    val colour = style != DarkStyle.BLACK
    // The accent's saturated container colour, not the pastel accent itself: tinting with the
    // pastel only makes a surface lighter, where this one gives it colour at the same brightness.
    fun at(own: Color, step: Int): Color {
        val level = levels[step] / 255f
        if (level == 0f) return Color.Black
        val neutral = own.scaledTo(level)
        return if (colour) {
            lerp(neutral, primaryContainer.scaledTo(level), (0.6f + 0.08f * step).coerceAtMost(0.95f))
        } else {
            primary.copy(alpha = 0.02f + 0.015f * step).compositeOver(neutral)
        }
    }
    return copy(
        // Grey text that read fine on grey looks dim on black, so it moves a little towards white.
        onSurfaceVariant = lerp(onSurfaceVariant, onSurface, 0.3f),
        background = at(background, 0),
        surface = at(surface, 0),
        surfaceDim = at(surfaceDim, 0),
        surfaceContainerLowest = at(surfaceContainerLowest, 0),
        surfaceContainerLow = at(surfaceContainerLow, 1),
        surfaceContainer = at(surfaceContainer, 2),
        surfaceContainerHigh = at(surfaceContainerHigh, 3),
        surfaceContainerHighest = at(surfaceContainerHighest, 4),
        surfaceBright = at(surfaceBright, 4),
        surfaceVariant = at(surfaceVariant, 3),
    )
}

/** This colour's hue at an average brightness of [level], 0 to 1. */
private fun Color.scaledTo(level: Float): Color {
    val mean = (red + green + blue) / 3f
    if (mean <= 0f) return Color(level, level, level)
    val k = level / mean
    return Color((red * k).coerceAtMost(1f), (green * k).coerceAtMost(1f), (blue * k).coerceAtMost(1f))
}
