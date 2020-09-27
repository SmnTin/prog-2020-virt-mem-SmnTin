package ru.emkn.virtualmemory.algorithms

data class Page(val index: Int)

data class Frame(
    val index: Int,
    val storedPage: Page? = null,
    val modified: Boolean = false
)

enum class OperationType {
    Read,
    Write
}

data class Query(val page: Page, val operationType: OperationType)

/**
 * Cache algorithm to control the strategy of frames management
 */
interface ReplacementAlgorithm {
    /**
     * Looks for the [Frame] that stores the [page]
     * @param page The page that the search is done for
     * @return [Frame] that stores the [page]
     * Null if [page] is not stored by any frame
     */
    fun findFrameStoringPage(page: Page): Frame?

    /**
     * According to some strategy picks a frame that
     * is supposed to be freed and reused by another page
     * later.
     * The strategy is implementation-defined.
     * Example implementations are LRU and FIFO.
     * It's guaranteed that free pages are given
     * sequentially in index increasing order until
     * the cache is full for the first time. After
     * that pages can be given in any order. That is
     * done for data locality.
     *
     * @return [Frame] chosen according to implementation-defined strategy
     */
    fun seekAnyFrame(): Frame

    /**
     * Puts [page] into frame with [frameIndex].
     * Throws [IllegalAccessException] if [page] is already
     * presented in the cache.
     *
     * @param page that would be stored in the frame.
     * Can be null, then the frame would store no page
     * @param frameIndex Index of frame that would store the [page]
     * Frame index is used instead of frame object because of two reasons:
     * 1. it's the only value that matters;
     * 2. passing frame object can give an illusion that changing
     * its fields would affect structures inside the Cache.
     */
    fun putPageIntoFrame(page: Page?, frameIndex: Int)
}