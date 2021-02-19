package slackdarwin

import com.slack.api.model.Message
import java.util.*

class MessagesContainer {
    var totalMessages = 0
    var messagesList = mutableListOf<SlackMessage>()

    fun addMessage(message: Message) {
        if (message.reactions != null) {
            val messageEmojiNames = message.reactions.map { it.name }
            val classification = MessageClassification.values().firstOrNull { classification ->
                classification.emojiNames.intersect(messageEmojiNames).any()
            }
            if (classification != null) {
                messagesList.add(toSlackMessage(message, classification))
                totalMessages += 1
            }
        }
    }

    private fun toSlackMessage(message: Message, classification: MessageClassification): SlackMessage =
        SlackMessage(
            messageText = message.text,
            messageDate = convertToDate(message.ts),
            classification = classification
        )

    private fun convertToDate(timestamp: String): Date {
        return timestamp.split(".").first().let { epochTime ->
            Date(epochTime.toLong() * 1000)
        }
    }

}
