package ru.emkn.virtualmemory.algorithms

/**
 * A segment tree implementation.
 * Range query, element update.
 * @param T element type
 * @param n Number of elements
 * @param e Neutral element
 * @param op Binary associative operation
 */
class SegmentTree<T>(val n: Int, val e: T, val op: (T, T) -> T) {
    private val t = MutableList(2 * n) { e }

    fun buildFrom(a: List<T>) {
        a.forEachIndexed { index, el ->
            t[n + index] = el
        }
        for (pos in (n - 1) downTo 1)
            t[pos] = op(t[2 * pos], t[2 * pos + 1])
    }

    fun updateElement(pos: Int, el: T) {
        var p = pos + n
        t[p] = el
        while (p > 0) {
            t[p] = op(t[2 * p], t[2 * p + 1])
            p /= 2
        }
    }

    fun query(left: Int, right: Int) : T {
        var l = n + left
        var r = n + right

        var lAns = e
        var rAns = e

        while (l < r) {
            if ((l and 1) == 1)
                lAns = op(lAns, t[l]); ++l
            if ((r and 1) == 0)
                rAns = op(t[r], rAns); --r
        }
        if (l == r)
            lAns = op(lAns, t[l])
        return op(lAns, rAns)
    }
}