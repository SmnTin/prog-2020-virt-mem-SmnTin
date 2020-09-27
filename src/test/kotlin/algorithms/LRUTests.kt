package algorithms

import org.junit.jupiter.api.*

import java.lang.IllegalArgumentException

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

import ru.emkn.virtualmemory.algorithms.LRU
import ru.emkn.virtualmemory.algorithms.Page

class LRUTests {
    @Test
    fun `test basic LRU behaviour`() {
        val numOfFrames = 2
        val numOfPages = 3

        val pages = List(numOfPages) { index ->
            Page(index)
        }

        val cache = LRU(numOfFrames)

        assertNull(cache.findFrameStoringPage(pages[1]))
        val frame1 = cache.seekAnyFrame()
        assertNull(frame1.storedPage)
        cache.putPageIntoFrame(pages[1], frame1.index)

        assertNull(cache.findFrameStoringPage(pages[2]))
        val frame2 = cache.seekAnyFrame()
        assertNull(frame2.storedPage)
        cache.putPageIntoFrame(pages[2], frame2.index)

        assertNull(cache.findFrameStoringPage(pages[0]))

        //updates frame1 usage counter
        cache.findFrameStoringPage(pages[1])

        val frame0 = cache.seekAnyFrame()
        assertEquals(frame1.index, frame0.index)

        cache.putPageIntoFrame(pages[0], frame0.index)
    }
}