package chatbot.dsl

import chatbot.api.Keyboard
import chatbot.api.Message
import chatbot.api.MessageId
import chatbot.bot.BotDSL

@BotDSL
class MessageBuilder(var message: Message) {
    var text: String = ""
    var replyTo: MessageId? = null
    var keyboard: Keyboard? = null

    fun removeKeyboard() {
        keyboard = Keyboard.Remove
    }

    fun withKeyboard(init: KeyboardBuilder.() -> Unit) {
        val keyboardBuilder = KeyboardBuilder().apply(init)
        keyboard = keyboardBuilder.build(keyboardBuilder.oneTime)
    }
}
