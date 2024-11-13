package chatbot.bot

import chatbot.api.ChatContext
import chatbot.api.ChatId
import chatbot.api.Client
import chatbot.api.Message
import chatbot.dsl.MessageBuilder

@BotDSL
class MessageProcessorContext<C : ChatContext?>(
    val message: Message,
    val client: Client,
    val context: C,
    val setContext: (c: ChatContext?) -> Unit,
) {
    fun sendMessage(chatId: ChatId, text: String) {
        client.sendMessage(chatId, text, null, null)
    }

    fun sendMessage(chatId: ChatId, processor: MessageBuilder.(ChatId) -> Unit) {
        val messageBuilder = MessageBuilder(message)
        messageBuilder.processor(chatId)
        if (messageBuilder.text.isEmpty() && messageBuilder.replyTo == null &&
            (messageBuilder.keyboard == null || messageBuilder.keyboard!!.isEmpty())
        ) {
            return
        }
        client.sendMessage(chatId, messageBuilder.text, messageBuilder.keyboard, messageBuilder.replyTo)
    }
}

typealias MessageProcessor<C> = MessageProcessorContext<C>.() -> Unit
