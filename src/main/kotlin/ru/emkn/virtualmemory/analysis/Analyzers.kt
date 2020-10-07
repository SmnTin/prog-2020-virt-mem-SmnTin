package ru.emkn.virtualmemory.analysis

import ru.emkn.virtualmemory.algorithms.*

data class AnalysisResult(
    val cache: Cache,
    val numOfFreedFrames: Int,
    val wasFrameFreedPerQuery: List<Boolean>
)

/**
 * Analyzes given [cache] by feeding it
 * with a given sequence of [pages] queries.
 * The analyzed criteria is the number of
 * queries that led to freeing of occupied
 * frame and/or loading a requested page into it
 *
 * @param cache Instance of analyzed cache
 * @param pages Sequence of queries to run on
 * @return The result of analysis
 */
fun analyzeConcreteCache(cache: Cache, pages: List<Page>) : AnalysisResult {
    val wasFrameFreedPerQuery = pages.map { page ->
        val found = cache.findFrameStoringPage(page)
        if (found == null)
            cache.putPageIntoFrame(
                page,
                cache.seekAnyFrame().index
            )
        found == null
    }

    val numOfFreedFrames = wasFrameFreedPerQuery.count { it }
    return AnalysisResult(cache, numOfFreedFrames, wasFrameFreedPerQuery)
}

/**
 * Creates instances of all the implemented caches
 * with a given number of frames and analyzes each
 * on a given sequence of pages.
 */
fun analyzeAllCaches(numOfFrames: Int, pages: List<Page>) : List<AnalysisResult> {
    val caches = listOf<Cache>(
        LRU(numOfFrames),
        FIFO(numOfFrames),
        OPT(numOfFrames, queries = pages)
    )
    return caches.map {cache ->
        analyzeConcreteCache(cache, pages)
    }
}
