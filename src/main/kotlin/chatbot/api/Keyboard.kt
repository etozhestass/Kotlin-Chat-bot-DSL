package chatbot.api

sealed interface Keyboard {
    fun isEmpty(): Boolean {
        return when (this) {
            is Markup -> keyboard.all { keyboard -> keyboard.isEmpty() }
            Remove -> false
        }
    }

    data object Remove : Keyboard
    data class Markup(
        val oneTime: Boolean,
        val keyboard: List<List<Button>>,
    ) : Keyboard

    data class Button(val text: String)
}
