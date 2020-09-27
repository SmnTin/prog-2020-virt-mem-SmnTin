package ru.emkn.virtualmemory.algorithms

import java.lang.IllegalArgumentException
import java.util.*

/**
 * Replacement algorithm implementing the LRU strategy
 * for pages seeking. When table is full the least recently
 * used frame is given otherwise one of the free frames is
 * given.
 * @constructor takes number of frames as a parameter and
 * constructs cache with that number of free frames
 */
class LRU(numOfFrames: Int) : BasicCache(numOfFrames) {
    override fun findFrameStoringPage(page: Page): Frame? {
        val frame = pagesToFramesMap[page]
        if (frame != null)
            framesWithInfo[frame.index].lastUsed = ++usageCounter
        return frame
    }

    override fun seekAnyFrame(): Frame {
        return framesQueueSortedByUsage.peek()!!.frame
    }

    override fun removeOldFrame(frameIndex: Int) {
        val frame = frames[frameIndex]
        pagesToFramesMap.remove(frame.storedPage)
        framesQueueSortedByUsage.remove(framesWithInfo[frameIndex])
    }

    override fun putUpdatedFrame(page: Page?, frameIndex: Int) {
        val newFrame = Frame(frameIndex, storedPage = page, modified = false)
        val newFrameWithInfo = FrameWithInfo(newFrame, lastUsed = ++usageCounter)

        frames[frameIndex] = newFrame
        framesWithInfo[frameIndex] = newFrameWithInfo
        framesQueueSortedByUsage.add(newFrameWithInfo)

        if (page != null)
            pagesToFramesMap[page] = newFrame
    }

    private data class FrameWithInfo(val frame: Frame, var lastUsed: Int)

    private val frames = Array(numOfFrames) { index ->
        Frame(index, storedPage = null)
    }

    private val framesWithInfo = Array(numOfFrames) { index ->
        FrameWithInfo(frames[index], lastUsed = 0)
    }

    private var usageCounter: Int = 0

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

