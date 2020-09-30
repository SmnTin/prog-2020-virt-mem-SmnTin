package ru.emkn.virtualmemory.algorithms

import java.lang.IllegalArgumentException
import java.util.*

/**
 * Replacement algorithm implementing the FIFO strategy
 * for pages seeking. When table is full the frame that
 * was used the first is given otherwise one of the free
 * frames is given.
 * @constructor takes number of frames as a parameter and
 * constructs cache with that number of free frames
 */
class FIFO(numOfFrames: Int) : BasicCache(numOfFrames) {
    override val name = "FIFO"

    override fun seekAnyFrame(): Frame {
        return if (freeFramesQueue.isNotEmpty())
            freeFramesQueue.element()
        else
            usedFramesQueue.element()
    }

    override fun removeOldFrame(frameIndex: Int) {
        val frame = frames[frameIndex]
        if (frame.storedPage != null) {
            pagesToFramesMap.remove(frame.storedPage)
            usedFramesQueue.remove(frame)
        } else {
            freeFramesQueue.remove(frame)
        }
    }

    override fun putUpdatedFrame(page: Page?, frameIndex: Int) {
        val newFrame = Frame(frameIndex, storedPage = page)
        frames[frameIndex] = newFrame
        if (page != null) {
            pagesToFramesMap[page] = newFrame
            usedFramesQueue.add(newFrame)
        } else {
            freeFramesQueue.add(newFrame)
        }
    }

    private val frames = Array(numOfFrames) { index ->
        Frame(index, storedPage = null)
    }

    private val freeFramesQueue: Queue<Frame> = LinkedList()
    private val usedFramesQueue: Queue<Frame> = LinkedList()

    init {
        for (frame in frames)
            freeFramesQueue.add(frame)
    }
}

