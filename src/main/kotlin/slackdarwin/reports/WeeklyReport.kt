package slackdarwin.reports

import slackdarwin.MessagesContainer
import slackdarwin.printers.Printer
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.MONDAY

class WeeklyReport(messagesContainer: MessagesContainer) : SlackMessagesReport(messagesContainer) {
    override fun print(printer: Printer) {
        messagesContainer.messagesList.groupBy { slackMessage ->
            Calendar.getInstance().also {
                it.time = slackMessage.messageDate
            }.let {
                it.set(Calendar.DAY_OF_WEEK, MONDAY)
                SimpleDateFormat("dd-MM-yyyy").format(it.time)
            }
        }.forEach{ (date, listOfMessages) ->
            printer.printLine("Week of $date:")
            printer.printLine("")
            printer.printLine("Total: ${listOfMessages.count()}")
            printPercentageOfMessagesForEachClassificationIn(printer, listOfMessages)
            printer.printLine("")
        }
    }
}
