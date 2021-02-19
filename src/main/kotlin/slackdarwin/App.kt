package slackdarwin

import slackdarwin.printers.ConsolePrinter
import slackdarwin.printers.Printer
import slackdarwin.reports.OverallSummaryReport
import slackdarwin.reports.WeeklyReport
import java.io.InputStream
import java.util.Properties

fun main(args: Array<String>) {
    try {
        showInfo()
        SlackDarwinApplication(args).also {
            println("Parsing messages...")
            it.buildContainer().also { container ->
                printReport(container, it.configuration)
            }
        }
    } catch (ex: InvalidArgsException) {
        showUsageInstructions(ex.message)
    }
}

private fun printReport(messagesContainer: MessagesContainer, configuration: SlackDarwinConfiguration) {
    println("Printing report...")
    val printer: Printer = ConsolePrinter()
    when (configuration.reportType){
        ReportType.SUMMARY -> OverallSummaryReport(messagesContainer)
        ReportType.WEEKLY -> WeeklyReport(messagesContainer)
    }.print(printer)
    println("End of report")
}


private fun showInfo() {
    println("Slack Darwin - Categorization of Slack Messages v${applicationProps()["version"]}")
}

private fun showUsageInstructions(errorMessage: String?) {
    println("Invalid input: $errorMessage")
    println("\nUsage:")
    println("$ java -jar slack-darwin.jar -TOKEN=[token]-CHANNEL=[channel]\n(optional: -LIMIT=[max number of messages], default to ${SlackDarwinApplication.DEFAULT_MESSAGE_LIMIT})\n(optional: -REPORT=[report type], default to ${SlackDarwinApplication.DEFAULT_REPORT_TYPE})")
}

private fun applicationProps() : Properties {
    val prop = Properties()
    val loader = Thread.currentThread().contextClassLoader
    val stream: InputStream = loader.getResourceAsStream("application.properties")!!
    prop.load(stream)
    return prop
}
