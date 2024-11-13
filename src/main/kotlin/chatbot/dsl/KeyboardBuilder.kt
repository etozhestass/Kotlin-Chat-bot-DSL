package chatbot.dsl

import chatbot.api.Keyboard
import chatbot.bot.BotDSL

@BotDSL
class KeyboardBuilder {
    var oneTime: Boolean = false

    var keyboard: MutableList<MutableList<Keyboard.Button>> = mutableListOf()

    operator fun MutableList<MutableList<Keyboard.Button>>.plusAssign(list: MutableList<Keyboard.Button>) {
        keyboard += listOf(list)
    }

    operator fun MutableList<Keyboard.Button>.plusAssign(button: Keyboard.Button) {
        this@plusAssign.add(button)
    }

    fun row(init: RowBuilder.() -> Unit) {
        val keyboardBuilder = RowBuilder()
        keyboardBuilder.init()
        keyboard.add(keyboardBuilder.row)
    }

    fun build(oneTime: Boolean): Keyboard = Keyboard.Markup(oneTime, keyboard)
}
