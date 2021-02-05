package slackdarwin

import com.slack.api.Slack
import com.slack.api.model.ConversationType
import com.slack.api.model.Message

class SlackDarwinApplication(applicationArgs: Array<String>) {
    val configuration = parseOptionsFromArgs(applicationArgs)
    val slackClient = Slack.getInstance().methods()

    companion object {
        const val TOKEN = "token"
        const val CHANNEL = "channel"
        const val LIMIT = "limit"
        const val DEFAULT_MESSAGE_LIMIT = 100
    }

    private fun parseOptionsFromArgs(args: Array<String>) : SlackDarwinConfiguration {
        var token : String? = null
        var channel: String? = null
        var limit: Int = DEFAULT_MESSAGE_LIMIT

        args.forEach {
            it.split("=").also { keyOption ->
                keyOption.first().toLowerCase().also { key ->
                    val value = keyOption.last()
                    if (key.contains(TOKEN)) {
                        token = value
                    }
                    if (key.contains(CHANNEL)) {
                        channel = value
                    }
                    if (key.contains(LIMIT)) {
                        limit = value.toInt()
                    }
                }
            }
        }

        if (token == null || channel == null) {
            throw InvalidArgsException("Wrong arguments given for parameters token and channel!")
        }

        return SlackDarwinConfiguration(
            token!!,
            channel!!,
            limit
        )
    }

    fun buildReport() : ClassificationReport {
        val targetChannel = slackClient.conversationsList{
            it
                .token(configuration.token)
                .types(listOf(ConversationType.PUBLIC_CHANNEL, ConversationType.PRIVATE_CHANNEL))
        }.channels.find { it.name == configuration.channel }
            ?: throw InvalidArgsException("Channel ${configuration.channel} not found!")
        slackClient.conversationsHistory {
            it
                .token(configuration.token)
                .channel(targetChannel.id)
                .limit(configuration.messagesLimit)
                .inclusive(true)
        }.also {
            return ClassificationReport().let { report ->
                it.messages.forEach{ message -> report.addMessage(message) }
                report
            }
        }
    }
}
