package ru.pavelapk.voice_assistant

enum class Action(val action: String) {
    DEFAULT("default"),
    ERROR("error"),
    CALC_ADD("calc.add"),
    STOP("smalltalk.confirmation.cancel");

    companion object {
        fun fromName(action: String) = values().firstOrNull { it.action == action } ?: DEFAULT
    }
}

