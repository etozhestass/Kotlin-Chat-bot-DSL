package chatbot.dsl

import chatbot.api.*
import chatbot.bot.BotDSL
import chatbot.bot.MessageHandler
import chatbot.bot.MessageProcessor
import chatbot.bot.MessageProcessorContext

@BotDSL
open class BehaviourBuilder<T : ChatContext?>(
    val client: Client,
    private var logLevel: LogLevel = LogLevel.ERROR,
    private var context: ChatContextsManager? = null,
    val handlers: MutableList<MessageHandler<T>> = mutableListOf(),
) {
    private var contextsManager
        get() = context
        set(value) {
            context = value
        }

    // behaviour
    @BotDSL
    fun onMessagePrefix(prefix: String, processor: MessageProcessor<T>) {
        onMessageTextPredicate(String::startsWith, prefix, processor)
    }

    @BotDSL
    fun onMessageContains(text: String, processor: MessageProcessorContext<T>.() -> Unit) {
        onPredicate({ message, _ -> message.text.contains(text) }, processor)
    }

    @BotDSL
    fun onMessage(textExactly: String, processor: MessageProcessorContext<T>.() -> Unit) {
        onPredicate({ message, _ -> message.text == textExactly }, processor)
    }

    @BotDSL
    fun onMessage(predicate: (Message) -> Boolean = { true }, processor: MessageProcessor<T>) {
        onPredicate({ message, _ -> predicate(message) }, processor)
    }

    @BotDSL
    fun onCommand(command: String, processor: MessageProcessor<T>) {
        onMessagePrefix("/$command", processor)
    }

    private fun onMessageTextPredicate(
        predicate: String.(String) -> Boolean,
        compareTo: String,
        processor: MessageProcessor<T>,
    ) {
        onPredicate({ message, _ -> message.text.predicate(compareTo) }, processor)
    }

    private fun onPredicate(predicate: (Message, T) -> Boolean, processor: MessageProcessor<T>) {
        addHandler(MessageHandler(predicate, processor))
    }

    private fun addHandler(handler: MessageHandler<T>) {
        handlers.add(handler)
    }

    inline fun <reified C : ChatContext?> wrapHandler(handler: MessageHandler<C>): MessageHandler<ChatContext?> {
        return MessageHandler({ message, context -> context is C && handler.predicate(message, context as C) }) {
            val context = this.context
            if (context is C) {
                val newProcessorContext =
                    MessageProcessorContext(this.message, this.client, context as C, this.setContext)
                handler.processor(newProcessorContext)
            }
        }
    }
}
