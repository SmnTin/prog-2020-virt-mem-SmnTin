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

interface ReplacementAlgorithm {
    fun findFrameStoringPage(page: Page): Frame?

    // The strategy of finding a frame is defined by implementation
    fun seekAnyFrame(): Frame

    // Frame index is used instead of frame object because of two reasons:
    // 1. it's the only value that matters;
    // 2. passing frame object can give an illusion that changing
    // its fields would affect structures inside the Cache.
    fun putPageIntoFrame(page: Page?, frameIndex: Int)
}