package ru.emkn.virtualmemory.analysis

import ru.emkn.virtualmemory.algorithms.*

data class AnalysisResult(
    val cache: Cache,
    val numOfFreedFrames: Int,
    val wasFrameFreedPerQuery: List<Boolean>
)

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
