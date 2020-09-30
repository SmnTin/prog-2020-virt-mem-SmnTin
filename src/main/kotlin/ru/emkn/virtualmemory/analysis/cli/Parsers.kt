package ru.emkn.virtualmemory.analysis.cli

import ru.emkn.virtualmemory.algorithms.Page
import ru.emkn.virtualmemory.cli.ParseException

fun parseListOfPages(str: String): List<Page> {
    try {
        val parsed = str
            .split(' ')
            .map { token -> Page(index = token.toInt()) }
        for (page in parsed)
            if (page.index < 0)
                throw ParseException("Page index must be positive.")
        return parsed
    } catch (e: ParseException) {
        throw e
    } catch (e: Throwable) {
        throw ParseException("Failed to parse input file.")
    }
}

fun parseSeveralListsOfPages(lines: List<String>): List<List<Page>> =
    lines.map { line -> parseListOfPages(line) }