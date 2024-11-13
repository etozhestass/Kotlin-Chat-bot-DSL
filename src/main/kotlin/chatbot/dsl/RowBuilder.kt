package chatbot.dsl

import chatbot.api.Keyboard
import chatbot.bot.BotDSL

@BotDSL
class RowBuilder {
    var row: MutableList<Keyboard.Button> = mutableListOf()

    fun button(text: String) {
        row += Keyboard.Button(text)
    }

    operator fun String.unaryMinus() {
        button(this)
    }
}
