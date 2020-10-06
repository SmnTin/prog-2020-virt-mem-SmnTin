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

    override fun findFrameStoringPage(page: Page): Frame? {
        val frame = super.findFrameStoringPage(page)
        if (frame != null) {
            val newFrameWithInfo = FrameWithInfo(frame, nextUsage = pollNewPageUsage(page))
            usedFrames.remove(framesWithInfo[frame.index])
            usedFrames.add(newFrameWithInfo)
            framesWithInfo[frame.index] = newFrameWithInfo
        }
        return frame
    }

    override fun seekAnyFrame(): Frame {
        return if (freeFrames.isEmpty()) {
            usedFrames.first().frame
        } else {
            freeFrames.first()
        }
    }

    override fun removeOldFrame(frameIndex: Int) {
        val frame = frames[frameIndex]
        val frameWithInfo = framesWithInfo[frameIndex]

        if (frame.storedPage != null) {
            usedFrames.remove(frameWithInfo)
            pagesToFramesMap.remove(frame.storedPage)
        } else {
            freeFrames.remove(frame)
        }
    }

    override fun putUpdatedFrame(page: Page?, frameIndex: Int) {
        val newFrame = Frame(frameIndex, storedPage = page)
        val newFrameWithInfo = FrameWithInfo(newFrame, pollNewPageUsage(page))

        if (page != null) {
            usedFrames.add(newFrameWithInfo)
            pagesToFramesMap[page] = newFrame
        } else {
            freeFrames.add(newFrame)
        }

        frames[frameIndex] = newFrame
        framesWithInfo[frameIndex] = newFrameWithInfo
    }

    private fun pollNewPageUsage(page: Page?) : Int {
        pagesToUsagesMap[page]?.poll()
        return pagesToUsagesMap[page]?.peek() ?: Int.MAX_VALUE
    }

    private val frames = Array(numOfFrames) { index ->
        Frame(index, storedPage = null)
    }
    private val framesWithInfo = Array(numOfFrames) { index ->
        FrameWithInfo(frames[index], nextUsage = Int.MAX_VALUE)
    }

    private val freeFrames = TreeSet(compareBy(Frame::index))
    private val usedFrames = TreeSet(compareByDescending(FrameWithInfo::nextUsage))

    private data class FrameWithInfo(val frame: Frame, val nextUsage: Int)
    private val pagesToUsagesMap : MutableMap<Page, Queue<Int>> = mutableMapOf()

    // fill freeFrames queue
    init {
        for (frame in frames)
            freeFrames.add(frame)
    }

    // fill structures with foreknown usage values
    init {
        queries.forEachIndexed { index, page ->
            if (pagesToUsagesMap[page] == null)
                pagesToUsagesMap[page] = LinkedList()
            pagesToUsagesMap[page]?.add(index)
        }
    }
}