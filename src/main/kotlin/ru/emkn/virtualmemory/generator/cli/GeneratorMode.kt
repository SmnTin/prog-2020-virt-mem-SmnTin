@file:OptIn(ExperimentalCli::class)

package ru.emkn.virtualmemory.generator.cli

import ru.emkn.virtualmemory.algorithms.Page
import ru.emkn.virtualmemory.cli.ParseException
import ru.emkn.virtualmemory.generator.generateRandomSequenceOfPages

import kotlinx.cli.*
import java.io.File
import java.io.IOException

/**
 * Subprogram to generate a random sequence of pages
 * and write it to a file which is then used as
 * input data in analysis mode.
 */
class GeneratorMode : Subcommand(
    name = "generate",
    actionDescription = "Generate random input data."
) {
    private val outputFileName by option(
        ArgType.String,
        fullName = "output_file",
        shortName = "of",
        description = "File where generator result would be written."
    ).required()

    private val numOfPages by option(
        ArgType.Int,
        fullName = "num_of_pages",
        shortName = "n",
        description = "Number of pages."
    ).required()

    private val sequenceLen by option(
        ArgType.Int,
        fullName = "seq_len",
        shortName = "l",
        description = "Length of result sequence."
    ).required()

    private fun checkArgs() {
        if (numOfPages <= 0 || sequenceLen <= 0)
            throw ParseException("Integer argument values must be positive.")
    }

    private fun writeResultToFile(result: List<Page>) {
        val outputFile = File(outputFileName)

        outputFile.writeText(result.joinToString(separator = " ") { page -> page.index.toString() } + "\n")
    }

    private fun generateResult(): List<Page> {
        return generateRandomSequenceOfPages(numOfPages)
            .take(sequenceLen)
            .toList()
    }

    override fun execute() {
        checkArgs()
        writeResultToFile(generateResult())
    }
}