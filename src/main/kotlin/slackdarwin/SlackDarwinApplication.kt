package slackdarwin

import com.slack.api.Slack
import com.slack.api.model.Conversation
import com.slack.api.model.ConversationType
import java.lang.IllegalArgumentException

class SlackDarwinApplication(applicationArgs: Array<String>) {
    val configuration = parseOptionsFromArgs(applicationArgs)
    val slackClient = Slack.getInstance().methods()

    companion object {
        val DEFAULT_REPORT_TYPE = ReportType.SUMMARY
        const val TOKEN = "token"
        const val CHANNEL = "channel"
        const val LIMIT = "limit"
        const val REPORT = "report"
        const val DEFAULT_MESSAGE_LIMIT = 100
        const val CHANNELS_LIMIT = 500
    }

    private fun parseOptionsFromArgs(args: Array<String>) : SlackDarwinConfiguration {
        var token : String? = null
        var channel: String? = null
        var limit: Int = DEFAULT_MESSAGE_LIMIT
        var reportType = DEFAULT_REPORT_TYPE

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
                    if (key.contains(REPORT)) {
                        try {
                            reportType = ReportType.valueOf(value)
                        } catch (ex: IllegalArgumentException) {
                            throw InvalidArgsException("Report type invalid! Valid values are: ${ReportType.values().map { r -> r.name }}")
                        }
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
            limit,
            reportType
        )
    }

    fun buildContainer() : MessagesContainer {
        var cursor: String? = null
        var targetChannel: Conversation? = null
        var checkedChannels: Int = CHANNELS_LIMIT
        do {
            print("Darwin is looking and classifying ${checkedChannels} samples\r")
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
                Thread.sleep(200)
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
            return MessagesContainer().let { report ->
                it.messages.forEach{
                    message -> report.addMessage(message)
                }
                report
            }
        }
    }
}
