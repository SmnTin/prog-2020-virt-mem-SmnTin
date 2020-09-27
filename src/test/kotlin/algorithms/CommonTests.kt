package algorithms

import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.Arguments

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

import ru.emkn.virtualmemory.algorithms.LRU
import ru.emkn.virtualmemory.algorithms.FIFO
import ru.emkn.virtualmemory.algorithms.OPT

import ru.emkn.virtualmemory.algorithms.Page
import ru.emkn.virtualmemory.algorithms.Cache
import java.lang.IllegalArgumentException

class CommonTests {
    @ParameterizedTest
    @MethodSource("cacheFactories")
    fun `throws on non-positive number of frames`(cacheFactory: (Int) -> Cache) {
        assertFailsWith<IllegalArgumentException> {
            cacheFactory(-10)
        }
        assertFailsWith<IllegalArgumentException> {
            cacheFactory(0)
        }
    }

    @ParameterizedTest
    @MethodSource("cacheFactories")
    fun `test sequential seeking of free pages`(cacheFactory: (Int) -> Cache) {
        val numOfFrames = 20
        val numOfPages = 20

        val pages = List(numOfPages) { index ->
            Page(index)
        }.shuffled()

        val framesIndices = List(numOfPages) { index ->
            index
        }

        val testPairs = pages.zip(framesIndices)

        val cache = cacheFactory(numOfFrames)

        for ((page, expectedFrameIndex) in testPairs) {
            assertNull(cache.findFrameStoringPage(page))

            val frame = cache.seekAnyFrame()
            assertNull(frame.storedPage)
            assertEquals(expectedFrameIndex, frame.index)

            cache.putPageIntoFrame(page, frame.index)
        }
    }

    @ParameterizedTest
    @MethodSource("cacheFactories")
    fun `test that seekAnyFrame() does not affect cache state`(cacheFactory: (Int) -> Cache) {
        val numOfFrames = 10

        val cache = cacheFactory(numOfFrames)

        assertEquals(cache.seekAnyFrame(), cache.seekAnyFrame())

        cache.putPageIntoFrame(Page(2), 1)
        assertEquals(cache.seekAnyFrame(), cache.seekAnyFrame())

        cache.putPageIntoFrame(Page(1), 0)
        assertEquals(cache.seekAnyFrame(), cache.seekAnyFrame())
    }

    @ParameterizedTest
    @MethodSource("cacheFactories")
    fun `test frame by page access`(cacheFactory: (Int) -> Cache) {
        val numOfFrames = 20
        val numOfPages = 20

        val cache = cacheFactory(numOfFrames)

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

    @ParameterizedTest
    @MethodSource("cacheFactories")
    fun `test cache throws on attempt to add an existing page`(cacheFactory: (Int) -> Cache) {
        val numOfFrames = 10
        val numOfPages = 5

        val cache = cacheFactory(numOfFrames)

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

    @ParameterizedTest
    @MethodSource("cacheFactories")
    fun `test frame freeing`(cacheFactory: (Int) -> Cache) {
        val numOfFrames = 2
        val numOfPages = 3
        val cache = cacheFactory(numOfFrames)

        val pages = List(numOfPages) { index ->
            Page(index)
        }

        cache.putPageIntoFrame(pages[0], cache.seekAnyFrame().index)
        cache.putPageIntoFrame(pages[1], cache.seekAnyFrame().index)

        val frame1 = cache.seekAnyFrame()
        // However, this frame was accessed last, it was freed so it
        // should be given first.
        cache.putPageIntoFrame(null, cache.seekAnyFrame().index)
        val frame2 = cache.seekAnyFrame()

        assertEquals(frame1.index, frame2.index)
    }

    companion object {
        @JvmStatic
        fun cacheFactories() = listOf(
            Arguments.of({ numOfFrames: Int -> LRU(numOfFrames) }),
            Arguments.of({ numOfFrames: Int -> FIFO(numOfFrames) }),
            Arguments.of({ numOfFrames: Int -> OPT(numOfFrames) })
        )
    }
}