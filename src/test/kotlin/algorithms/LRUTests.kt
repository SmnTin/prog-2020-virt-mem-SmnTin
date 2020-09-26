package algorithms

import org.junit.jupiter.api.*

import java.lang.IllegalArgumentException
import java.util.stream.IntStream
import java.util.stream.Stream

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

import ru.emkn.virtualmemory.algorithms.LRU
import ru.emkn.virtualmemory.algorithms.Page

class LRUTests {
    @Test
    fun `throws on non-positive number of frames`() {
        assertFailsWith<IllegalArgumentException> {
            LRU(-10)
        }
        assertFailsWith<IllegalArgumentException> {
            LRU(0)
        }
    }

    @Test
    fun `test sequential seeking of free pages`() {
        val numOfFrames = 20
        val numOfPages = 20

        val pages = List(numOfPages) { index ->
            Page(index)
        }.shuffled()

        val framesIndices = List(numOfPages) { index ->
            index
        }

        val testPairs = pages.zip(framesIndices)

        val cache = LRU(numOfFrames)

        for ((page, expectedFrameIndex) in testPairs) {
            assertNull(cache.findFrameStoringPage(page))

            val frame = cache.seekAnyFrame()
            assertNull(frame.storedPage)
            assertEquals(expectedFrameIndex, frame.index)

            cache.putPageIntoFrame(page, frame.index)
        }
    }

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

    @Test
    fun `test that seekAnyFrame() does not affect cache state`() {
        val numOfFrames = 10

        val cache = LRU(numOfFrames)

        assertEquals(cache.seekAnyFrame(), cache.seekAnyFrame())

        cache.putPageIntoFrame(Page(2), 1)
        assertEquals(cache.seekAnyFrame(), cache.seekAnyFrame())

        cache.putPageIntoFrame(Page(1), 0)
        assertEquals(cache.seekAnyFrame(), cache.seekAnyFrame())
    }

    @Test
    fun `test frame by page access`() {
        val numOfFrames = 20
        val numOfPages = 20

        val cache = LRU(numOfFrames)

        val pages = List(numOfPages) { index ->
            Page(index)
        }.shuffled()

        val frames = pages.map { page ->
            val frame = cache.seekAnyFrame()
            cache.putPageIntoFrame(page, frame.index)
            frame
        }

        val testedPairs = (pages zip frames).shuffled()

        for ((page, frame) in testedPairs)
            assertEquals(frame.index, cache.findFrameStoringPage(page)?.index)
    }

    @Test
    fun `test cache throws on attempt to add an existing page`() {
        val numOfFrames = 10
        val numOfPages = 5

        val cache = LRU(numOfFrames)

        val pages = List(numOfPages) { index ->
            Page(index)
        }.shuffled()

        pages.forEach { page ->
            cache.putPageIntoFrame(page, cache.seekAnyFrame().index)
        }

        val pages2 = pages.shuffled()

        pages2.forEach { page ->
            assertFailsWith<IllegalAccessException> {
                cache.putPageIntoFrame(page, cache.seekAnyFrame().index)
            }
        }
    }
}