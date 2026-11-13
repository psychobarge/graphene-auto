package app.seb3thehacker.gearslip.car

import android.view.KeyEvent
import org.junit.Assert.assertTrue
import org.junit.Test

class CarKeysTest {
    // Renault R-Link 2 presses Play about 1.5s after connecting, before it grants the screen
    // (logs/2026-11-13): this used to throw and end every session.
    @Test fun `media key before the car screen is up is ignored, not fatal`() {
        assertTrue(CarServices.mediaOrNull() == null)
        for (code in listOf(KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_MEDIA_NEXT)) {
            assertTrue(CarKeys.onKey(code, down = true, longPress = false))
        }
    }
}
