package algorithms

import org.junit.jupiter.api.*

import java.lang.IllegalArgumentException

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

import ru.emkn.virtualmemory.algorithms.FIFO
import ru.emkn.virtualmemory.algorithms.Page

class FIFOTests {
    @Test
    fun `test basic FIFO behaviour`() {
        val numOfFrames = 20
        val numOfPages = 22

        val cache = FIFO(numOfFrames)

        val pages = List(numOfPages) { index ->
            Page(index)
        }.shuffled()

        val frame0 = cache.seekAnyFrame()
        cache.putPageIntoFrame(pages[0], frame0.index)

        val frame1 = cache.seekAnyFrame()
        cache.putPageIntoFrame(pages[1], frame1.index)

        pages.subList(2, 20).forEach { page ->
            cache.putPageIntoFrame(page, cache.seekAnyFrame().index)
        }

        val frame2 = cache.seekAnyFrame()
        assertEquals(frame0.index, frame2.index)
        cache.putPageIntoFrame(pages[20], frame2.index)

        val frame3 = cache.seekAnyFrame()
        assertEquals(frame1.index, frame3.index)
        cache.putPageIntoFrame(pages[21], frame3.index)
    }
}