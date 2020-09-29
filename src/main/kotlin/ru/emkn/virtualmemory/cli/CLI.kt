@file:OptIn(ExperimentalCli::class)

package ru.emkn.virtualmemory.cli

import kotlinx.cli.*
import ru.emkn.virtualmemory.analysis.cli.AnalysisMode
import ru.emkn.virtualmemory.generator.cli.GeneratorMode

fun executeWithArgs(args: Array<String>) {
    val parser = ArgParser("Virtual memory cache strategies analyzer")

    parser.subcommands(AnalysisMode(), GeneratorMode())

    parser.parse(args)
}