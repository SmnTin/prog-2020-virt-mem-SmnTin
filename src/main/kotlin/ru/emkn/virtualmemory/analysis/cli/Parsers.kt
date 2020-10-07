package ru.emkn.virtualmemory.analysis.cli

import ru.emkn.virtualmemory.algorithms.Page
import ru.emkn.virtualmemory.cli.ParseException

/**
 * Parses pages indices from input string and converts
 * them to Pages with respective indices.
 * @throws ParseException
 */
fun parseListOfPages(str: String): List<Page> {
    try {
        val parsed = str
            .split(' ')
            .map { token -> Page(index = token.toInt()) }
        for (page in parsed)
            if (page.index < 0)
                throw ParseException("Page index must be positive.")
        return parsed
    } catch (e: NumberFormatException) {
        throw ParseException("Failed to parse one of the numbers in the input file: " + e.message)
    }
}

/**
 * @throws ParseException
 */
fun parseSeveralListsOfPages(lines: List<String>): List<List<Page>> =
    lines.map { line -> parseListOfPages(line) }