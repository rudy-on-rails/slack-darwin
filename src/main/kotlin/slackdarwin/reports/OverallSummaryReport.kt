package slackdarwin.reports

import slackdarwin.MessagesContainer
import slackdarwin.printers.Printer

class OverallSummaryReport(messagesContainer: MessagesContainer) : SlackMessagesReport(messagesContainer) {
    override fun print(printer: Printer) {
        printer.printLine("Overall Messages Report:")
        printer.printLine("")
        printer.printLine("Classification:")
        printer.printLine("Total messages: ${messagesContainer.totalMessages}")
        printer.printLine("")
        printPercentageOfMessagesForEachClassificationIn(printer, messagesContainer.messagesList)
    }
}
