package app.seb3thehacker.gearslip.audio

import app.seb3thehacker.gearslip.Protobuf
import app.seb3thehacker.gearslip.ServiceDiscovery
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The 2026-11-16 R-Link 2 drive: the car took the speakers (LOSS) and the music never came back,
 * even after the driver pressed play again. Sound heard after a loss must ask the car again.
 */
class AudioLinkResumeTest {

    private val focusRequests = mutableListOf<ByteArray>()

    private fun link(resumeAfterLossMs: Long) = AudioLink(
        ServiceDiscovery.AudioService(4, ServiceDiscovery.STREAM_MEDIA, ServiceDiscovery.CODEC_PCM, listOf(ServiceDiscovery.AudioConfig(48_000, 16, 2))),
        sendOnChannel = { _, _ -> },
        sendControl = { id, body -> if (id == AudioLink.MSG_FOCUS_REQUEST) focusRequests += body },
        onReady = {},
        onClose = {},
        resumeAfterLossMs = resumeAfterLossMs,
    ).apply {
        onMessage(32771, Protobuf.varintField(1, 2)) // Config { status = READY }
        start()
        onFocus(1) // GAIN
        onFocus(3) // LOSS: stops and releases the speakers
    }

    private val gain = Protobuf.varintField(1, 1).toList()

    @Test
    fun soundRightAfterTheLossIsLeftAlone() {
        val link = link(resumeAfterLossMs = 60_000)
        val before = focusRequests.size
        link.heard()
        assertEquals(before, focusRequests.size)
        assertTrue(!link.streaming)
    }

    @Test
    fun soundAfterTheWaitAsksForTheSpeakersOnce() {
        val link = link(resumeAfterLossMs = 0)
        focusRequests.clear()
        link.heard()
        link.heard()
        assertEquals(listOf(gain), focusRequests.map { it.toList() })
        assertTrue(link.streaming)
    }
}
