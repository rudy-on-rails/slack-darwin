package slackdarwin

enum class MessageClassification(var emojiNames: List<String>){
    BUG(listOf("bug")),
    QUESTION(listOf("question")),
    FEEDBACK(listOf("recycle")),
    SYSTEM_WIDE_ISSUE(listOf("matrix"))
}
