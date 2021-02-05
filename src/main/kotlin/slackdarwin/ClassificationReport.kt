package slackdarwin

import com.slack.api.model.Message

class ClassificationReport {
    var totalMessages = 0
    private var messagesPerClassification = mutableMapOf<MessageClassification, MutableList<String>>()

    fun addMessage(message: Message) {
        if (message.reactions != null) {
            val messageEmojiNames = message.reactions.map { it.name }
            val classification = MessageClassification.values().firstOrNull { classification ->
                classification.emojiNames.intersect(messageEmojiNames).any()
            }
            if (classification != null) {
                if (messagesPerClassification[classification] == null) {
                    messagesPerClassification[classification] = mutableListOf()
                }
                messagesPerClassification[classification]!!.add(message.text)
                totalMessages += 1
            }
        }
    }

    fun percentageOfMessages(classification: MessageClassification) : Double {
        if (totalMessages == 0) {
            return 0.00
        }
        return messagesPerClassification[classification]?.let {
            (it.count().toDouble() / totalMessages) * 100
        } ?: 0.00
    }
}
