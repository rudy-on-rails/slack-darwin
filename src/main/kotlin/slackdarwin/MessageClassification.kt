package slackdarwin

enum class MessageClassification(var emojiNames: List<String>){
    BUG(listOf("bug")),
    QUESTION(listOf("question")),
    USER_ERROR(listOf("dusty_stick"))
}
