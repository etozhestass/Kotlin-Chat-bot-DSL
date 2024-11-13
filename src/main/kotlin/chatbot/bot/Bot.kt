package chatbot.bot

import chatbot.api.*
import chatbot.dsl.IntoBuilder

@DslMarker
annotation class BotDSL

class Bot(
    private val client: Client,
    override var logLevel: LogLevel = LogLevel.ERROR,
    private val messageHandlers: MutableList<MessageHandler<ChatContext?>> = mutableListOf(),
    private var contextManager: ChatContextsManager? = null,
) : ChatBot {
    override fun processMessages(message: Message) {
        if (logLevel == LogLevel.INFO) {
            println("[INFO] precessing message $message")
        }
        val context = contextManager?.getContext(message.chatId)
        for (handler in messageHandlers) {
            if (handler.predicate(message, context)) {
                val setContest: (ChatContext?) -> Unit = { contextManager?.setContext(message.chatId, it) }
                MessageProcessorContext(message, client, context, setContest)
                    .apply(handler.processor)
                break
            }
        }
    }

    fun behaviour(init: IntoBuilder.() -> Unit) {
        val behaviourBuilder = IntoBuilder(client)
        behaviourBuilder.init()
        messageHandlers.addAll(behaviourBuilder.handlers)
    }

    fun use(logLevel: LogLevel) {
        this.logLevel = logLevel
    }

    fun use(anotherContextManager: ChatContextsManager?) {
        this.contextManager = anotherContextManager
    }

    operator fun LogLevel.unaryPlus() =
        use(this)
}

fun chatBot(client: Client, builder: Bot.() -> Unit): ChatBot {
    return Bot(client).apply(builder)
}
