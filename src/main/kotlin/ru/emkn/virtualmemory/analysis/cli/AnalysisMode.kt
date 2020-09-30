@file:OptIn(ExperimentalCli::class)

package ru.emkn.virtualmemory.analysis.cli

import kotlinx.cli.*
import ru.emkn.virtualmemory.analysis.AnalysisResult
import ru.emkn.virtualmemory.analysis.analyzeAllCaches
import ru.emkn.virtualmemory.algorithms.Page
import java.io.File

/**
 * Main mode. Analyzes all the cache strategies on given data and
 * writes results to a file.
 *
 * Several input sequences can be listed on multiple lines in the
 * input file result of each sequence analysis is written in a
 * separate file.
 */
class AnalysisMode : Subcommand(
    name = "analyze",
    actionDescription = "Analyze different strategies"
) {
    private val numOfFrames by option(
        ArgType.Int,
        shortName = "m",
        fullName = "num_of_frames",
        description = "Number of frames."
    ).required()

    private val inputFileName by option(
        ArgType.String,
        fullName = "input_file",
        shortName = "if",
        description = "File from where input data is retrieved."
    ).required()

    private val outputFileName by option(
        ArgType.String,
        fullName = "output_file",
        shortName = "of",
        description = """
                File where analysis result would be written.
                A pair of "<cache name> <num of freed frames>"
                on each line sorted in descending order.
                """.trimIndent()
    ).required()

    private fun writeResultToFile(filename: String, result: List<AnalysisResult>) {
        val outputFile = File(filename)

        val writer = outputFile.writer()

        for (result in result.sortedByDescending(AnalysisResult::numOfFreedFrames))
            writer.write("${result.cache.name} ${result.numOfFreedFrames}\n")

        writer.close()
    }

    /**
     * Writes each result to a separate file with a filename + "_index" suffix.
     */
    private fun writeMultipleResultToFiles(filename: String, multipleResult: List<List<AnalysisResult>>) {
        multipleResult.forEachIndexed { index, result ->
            writeResultToFile(filename + "_$index", result)
        }
    }

    private fun readInputFile(): List<List<Page>> {
        val inputFile = File(inputFileName)

        return parseSeveralListsOfPages(inputFile.readLines())
    }

    private fun singleMode(input: List<Page>) {
        writeResultToFile(outputFileName, analyzeAllCaches(numOfFrames, input))
    }

    private fun multipleMode(multipleInput: List<List<Page>>) {
        writeMultipleResultToFiles(outputFileName,
            multipleInput.map { input ->
                analyzeAllCaches(numOfFrames, input)
            }
        )
    }

    override fun execute() {
        val input = readInputFile()
        if (input.size == 1)
            singleMode(input.first())
        else
            multipleMode(input)
    }
}