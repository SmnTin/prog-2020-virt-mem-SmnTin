package ru.emkn.virtualmemory.algorithms

import java.util.*

/**
 * Strategy that needs to foreknow the pages that the cache
 * would be asked to store. It is not implementable in
 * practice. But can be useful in theory to evaluate
 * efficiency of other algorithms.
 *
 * This implementation has all the guarantees that were
 * defined for the [Cache] interface.
 */
class OPT(numOfFrames: Int, queries: List<Page> = emptyList()) : BasicCache(numOfFrames) {
    override val name = "OPT"

    override fun seekAnyFrame(): Frame {
        return if (freeFrames.isEmpty()) {
            val leastUsedPage: Page? = if (queueOfPagesWithInfo.isNotEmpty())
                queueOfPagesWithInfo.peek().page
            else
                null
            pagesToFramesMap[leastUsedPage] ?: usedFrames.first()
        } else {
            freeFrames.peek()
        }
    }

    override fun removeOldFrame(frameIndex: Int) {
        val frame = frames[frameIndex]
        if (frame.storedPage != null) {
            usedFrames.remove(frame)
            pagesToFramesMap.remove(frame.storedPage)
        } else {
            freeFrames.remove(frame)
        }
    }

    override fun putUpdatedFrame(page: Page?, frameIndex: Int) {
        val newFrame = Frame(frameIndex, storedPage = page)

        if (page != null) {
            decrementPageUsageCounter(page)
            usedFrames.add(newFrame)
            pagesToFramesMap[page] = newFrame
        } else {
            freeFrames.add(newFrame)
        }

        frames[frameIndex] = newFrame
    }

    private fun decrementPageUsageCounter(page: Page) {
        if (page in pagesToUsageCounterMap)
            queueOfPagesWithInfo.remove(PageWithInfo(page, pagesToUsageCounterMap[page]!!))

        val updatedUsageCounter = pagesToUsageCounterMap.getOrDefault(page, 0) - 1
        pagesToUsageCounterMap[page] = updatedUsageCounter
        queueOfPagesWithInfo.add(PageWithInfo(page, updatedUsageCounter))
    }

    private val frames = Array(numOfFrames) { index ->
        Frame(index, storedPage = null)
    }
    private val freeFrames = PriorityQueue<Frame>(compareBy { it.index })
    private val usedFrames: MutableSet<Frame> = mutableSetOf()

    private data class PageWithInfo(val page: Page, val usageCounter: Int)
    private val queueOfPagesWithInfo = PriorityQueue<PageWithInfo>(compareBy { it.usageCounter })
    private val pagesToUsageCounterMap: MutableMap<Page, Int> = mutableMapOf()

    // fill freeFrames queue
    init {
        for (frame in frames)
            freeFrames.add(frame)
    }

    // fill structures with foreknown usage values
    init {
        for (page in queries)
            pagesToUsageCounterMap.merge(page, 1, Int::plus)
        for (page in queries)
            queueOfPagesWithInfo.add(PageWithInfo(page, pagesToUsageCounterMap[page]!!))
    }
}