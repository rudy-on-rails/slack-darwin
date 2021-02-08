package slackdarwin

import com.slack.api.Slack
import com.slack.api.model.Channel
import com.slack.api.model.Conversation
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
        const val CHANNELS_LIMIT = 500
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
        var cursor: String? = null
        var targetChannel: Conversation? = null
        var checkedChannels: Int = CHANNELS_LIMIT
        do {
            println("Checked $checkedChannels channels...")
            slackClient.conversationsList {
                it
                    .token(configuration.token)
                    .types(listOf(ConversationType.PUBLIC_CHANNEL, ConversationType.PRIVATE_CHANNEL))
                    .limit(CHANNELS_LIMIT)
                    .excludeArchived(true)
                    .cursor(cursor)
            }.also { conversationsListResponse ->
                targetChannel = conversationsListResponse.channels.find { it.name == configuration.channel }
                cursor = conversationsListResponse.responseMetadata.nextCursor
                checkedChannels += CHANNELS_LIMIT
                Thread.sleep(500)
            }

        } while (cursor != null && targetChannel == null)

        if (targetChannel == null) {
            throw InvalidArgsException("Channel ${configuration.channel} not found!")
        }

        slackClient.conversationsHistory {
            it
                .token(configuration.token)
                .channel(targetChannel!!.id)
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
