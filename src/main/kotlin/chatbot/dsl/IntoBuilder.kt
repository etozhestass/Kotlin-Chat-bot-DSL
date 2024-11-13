package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.api.ChatContextsManager
import chatbot.api.Client
import chatbot.api.LogLevel
import chatbot.bot.BotDSL
import chatbot.bot.MessageHandler
import chatbot.bot.MessageProcessorContext

@BotDSL
class IntoBuilder(
    client: Client,
    logLevel: LogLevel = LogLevel.ERROR,
    handlers: MutableList<MessageHandler<ChatContext?>> = mutableListOf(),
    context: ChatContextsManager? = null,
) : BehaviourBuilder<ChatContext?>(client, logLevel, context, handlers) {

    inline fun <reified U : ChatContext?> into(behaviourBuilder: BehaviourBuilder<U>.() -> Unit) {
        intoImpl<U>(behaviourBuilder) { chatContext -> chatContext is U }
    }

    inline infix fun <reified U : ChatContext?> U.into(behaviourBuilder: BehaviourBuilder<U>.() -> Unit) {
        intoImpl<U>(behaviourBuilder) { chatContext -> chatContext == this }
    }

    inline fun <reified U : ChatContext?> intoImpl(
        behaviourBuilder: BehaviourBuilder<U>.() -> Unit,
        crossinline checkInstance: (ChatContext?) -> Boolean,
    ) {
        val builder = BehaviourBuilder<U>(client)
        builder.behaviourBuilder()
        handlers.addAll(builder.handlers.map { toNullableContextHandler(it, checkInstance) })
    }

    inline fun <reified C : ChatContext?> toNullableContextHandler(
        messageHandler: MessageHandler<C>,
        crossinline checkContext: (ChatContext?) -> Boolean,
    ): MessageHandler<ChatContext?> {
        return MessageHandler({ message, context ->
            context is C && messageHandler.predicate(message, context as C) && checkContext(context)
        }) {
            if (context is C) {
                val newProcessorContext =
                    MessageProcessorContext(this.message, this.client, context as C, this.setContext)
                messageHandler.processor(newProcessorContext)
            }
        }
    }
}
