package slackdarwin

import com.slack.api.model.Message
import java.math.BigDecimal
import java.math.RoundingMode

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

    fun percentageOfMessages(classification: MessageClassification) : BigDecimal {
        if (totalMessages == 0) {
            return 0.00.toBigDecimal()
        }
        return messagesPerClassification[classification]?.let {
            (it.count().toDouble() / totalMessages) * 100
        }?.toBigDecimal()?.setScale(2, RoundingMode.DOWN) ?: 0.00.toBigDecimal()
    }
}
