package ru.pavelapk.voice_assistant

class SuperNeuralTextProcessing {
    fun process(input: String): String {
        return when (input.lowercase()) {
            "привет" -> "Здравствуй"
            "как дела" -> "Нормально"
            "майнкрафт" -> "Это моя жизнь"
            "minecraft" -> "Это моя жизнь"

            else -> "Я вас не понимаю"
        }
    }
}