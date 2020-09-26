package ru.emkn.virtualmemory.algorithms

import java.lang.IllegalArgumentException
import java.util.*

class LRU(val numOfFrames: Int) : ReplacementAlgorithm {
    init {
        if (numOfFrames <= 0)
            throw IllegalArgumentException("Number of frames must be positive.")
    }

    override fun findFrameStoringPage(page: Page): Frame? {
        val frameWithInfo = pagesToFramesMap[page]
        if (frameWithInfo != null)
            frameWithInfo.lastUsed = ++usageCounter
        return frameWithInfo?.frame
    }

    override fun seekAnyFrame(): Frame {
        return framesQueueSortedByUsage.peek()!!.frame
    }

    override fun putPageIntoFrame(page: Page?, frameIndex: Int) {
        if (pagesToFramesMap[page] != null)
            throw IllegalAccessException("The page is already stored inside one of the frames.")

        removeFrameFromStructures(frameIndex)
        putUpdatedFrameStoringPage(frameIndex, page)
    }

    private fun removeFrameFromStructures(frameIndex: Int) {
        val frame = frames[frameIndex]
        pagesToFramesMap.remove(frame.storedPage)
        framesQueueSortedByUsage.remove(framesWithInfo[frame.index])
    }

    private fun putUpdatedFrameStoringPage(frameIndex: Int, page: Page?) {
        val newFrame = Frame(frameIndex, storedPage = page, modified = false)
        val newFrameWithInfo = FrameWithInfo(newFrame, lastUsed = ++usageCounter)

        frames[frameIndex] = newFrame
        framesWithInfo[frameIndex] = newFrameWithInfo
        framesQueueSortedByUsage.add(newFrameWithInfo)

        if (page != null)
            pagesToFramesMap[page] = newFrameWithInfo
    }

    private data class FrameWithInfo(val frame: Frame, var lastUsed: Int)

    private val frames = Array(numOfFrames) { index ->
        Frame(index, storedPage = null)
    }

    private val framesWithInfo = Array(numOfFrames) { index ->
        FrameWithInfo(frames[index], lastUsed = 0)
    }

    private var usageCounter: Int = 0

    private val pagesToFramesMap: MutableMap<Page, FrameWithInfo> = mutableMapOf()

    private val framesQueueSortedByUsage =
        PriorityQueue<FrameWithInfo>(
            compareBy(
                { it.frame.storedPage != null },
                { it.lastUsed }, { it.frame.index }
            )
        )

    init {
        for (frame in framesWithInfo)
            framesQueueSortedByUsage.add(frame)
    }
}

// Returns number of queries that led to page unloading
//fun analyzeLRU(queries: List<Query>): List<Boolean> {
//
//}

