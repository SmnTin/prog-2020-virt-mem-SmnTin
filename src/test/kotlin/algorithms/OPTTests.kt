package algorithms

import org.junit.jupiter.api.*

import java.lang.IllegalArgumentException

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

import ru.emkn.virtualmemory.algorithms.OPT
import ru.emkn.virtualmemory.algorithms.Page

class OPTTests {
    @Test
    fun `test basic LRU behaviour`() {
        val numOfFrames = 3
        val numOfPages = 3

        val pages = List(numOfPages) { index ->
            Page(index)
        }

        val queries = listOf(0, 1, 2, 1, 0, 2, 2)
            .map { id ->
                pages[id]
            }

        val cache = OPT(numOfFrames, queries)
        for (page in pages)
            cache.putPageIntoFrame(page, cache.seekAnyFrame().index)

        assertEquals(pages[2], cache.seekAnyFrame().storedPage)
    }

    companion object {
        @CacheFactory
        @JvmStatic
        fun factoryForOPT(numOfFrames: Int) : OPT =
            OPT(numOfFrames)
    }
}