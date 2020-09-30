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
        val numOfPages = 4

        val pages = List(numOfPages) { index ->
            Page(index)
        }

        val queries = listOf(0, 1, 2, 3, 0, 1, 2, 2, 2, 0, 3, 3, 3, 3, 3)
            .shuffled()
            .map { id ->
                pages[id]
            }

        val cache = OPT(numOfFrames, queries)

        pages.take(3).forEach { page ->
            cache.putPageIntoFrame(page, cache.seekAnyFrame().index)
        }
        assertEquals(pages[1], cache.seekAnyFrame().storedPage)
        cache.putPageIntoFrame(pages[3], cache.seekAnyFrame().index)

        assertEquals(pages[0], cache.seekAnyFrame().storedPage)
    }
}