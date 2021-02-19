package slackdarwin.reports

import slackdarwin.MessageClassification
import slackdarwin.MessagesContainer
import slackdarwin.SlackMessage
import slackdarwin.printers.Printer
import java.math.BigDecimal
import java.math.RoundingMode

abstract class SlackMessagesReport(val messagesContainer: MessagesContainer) {
    abstract fun print(printer: Printer)

    protected fun printPercentageOfMessagesForEachClassificationIn(printer: Printer, messagesList: List<SlackMessage>) {
        MessageClassification
            .values()
            .forEach {
                printer.printLine("Percentage of $it : ${percentageOfMessages(it, messagesList)}%")
            }
    }

    private fun percentageOfMessages(classification: MessageClassification, messagesList: List<SlackMessage>) : BigDecimal {
        val messagesPerClassification = messagesList.groupBy { it.classification }

        if (messagesList.count() == 0) {
            return 0.00.toBigDecimal()
        }

        return messagesPerClassification[classification]?.let {
            (it.count().toDouble() / messagesList.count()) * 100
        }?.toBigDecimal()?.setScale(2, RoundingMode.DOWN) ?: 0.00.toBigDecimal()
    }
}
