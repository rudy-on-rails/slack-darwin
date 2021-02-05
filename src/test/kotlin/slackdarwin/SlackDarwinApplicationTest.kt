package slackdarwin

import junit.framework.TestCase.assertEquals
import org.junit.Test

class SlackDarwinApplicationTest {
    @Test
    fun `should generate parsed configuration correctly from the args`() {
        val args = arrayOf("token=1234","-channel=#mychannel", "limit=10000")
        SlackDarwinApplication(args).configuration.also {
            assertEquals("1234", it.token)
            assertEquals("#mychannel", it.channel)
            assertEquals(10000, it.messagesLimit)
        }
    }
}
