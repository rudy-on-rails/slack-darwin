package slackdarwin

import java.util.*

data class SlackMessage(
    val messageText: String,
    val messageDate: Date,
    val classification: MessageClassification
)
