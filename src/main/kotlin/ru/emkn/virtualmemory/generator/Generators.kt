package ru.emkn.virtualmemory.generator

import ru.emkn.virtualmemory.algorithms.Page
import kotlin.random.Random

fun generateRandomSequenceOfPages(numberOfPages: Int) : Sequence<Page> {
    return generateSequence {
        Page(index = Random.nextInt(numberOfPages))
    }
}