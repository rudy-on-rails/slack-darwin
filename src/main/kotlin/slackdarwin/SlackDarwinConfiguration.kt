package slackdarwin

data class SlackDarwinConfiguration(
    val token: String,
    val channel: String,
    val messagesLimit: Int = 500,
    val reportType: ReportType
)
