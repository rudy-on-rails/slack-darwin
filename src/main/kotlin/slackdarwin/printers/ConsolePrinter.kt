package slackdarwin.printers

class ConsolePrinter : Printer {
    override fun printLine(lineContent: String) {
       println(lineContent)
    }
}
