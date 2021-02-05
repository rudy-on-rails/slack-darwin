package slackdarwin

import java.io.InputStream
import java.util.*

fun main(args: Array<String>) {
    try {
        showInfo()
        SlackDarwinApplication(args).also {
            println("Parsing messages...")
            it.buildReport().also { report ->
                printReport(report)
            }
        }
    } catch (ex: InvalidArgsException) {
        showUsageInstructions(ex.message)
    }
}

fun printReport(report: ClassificationReport) {
    println("\nClassification:")
    println("Total messages: ${report.totalMessages}\n")
    MessageClassification
        .values()
        .forEach {
        println("Percentage of $it : ${report.percentageOfMessages(it)}%")
    }
}

fun showInfo() {
    println("Slack Darwin - Categorization of Slack Messages v${applicationProps()["version"]}")
}

fun showUsageInstructions(errorMessage: String?) {
    println("Invalid input: $errorMessage")
    println("\nUsage:")
    println("$ java -jar slack-darwin.jar -TOKEN=[token] -CHANNEL=[channel] (optional: -LIMIT=[max number of messages], default to ${SlackDarwinApplication.DEFAULT_MESSAGE_LIMIT})")
}

fun applicationProps() : Properties {
    val prop = Properties()
    val loader = Thread.currentThread().contextClassLoader
    val stream: InputStream = loader.getResourceAsStream("application.properties")!!
    prop.load(stream)
    return prop
}