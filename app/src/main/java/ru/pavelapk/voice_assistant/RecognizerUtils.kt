package ru.pavelapk.voice_assistant

import android.content.Intent
import android.speech.RecognizerIntent

object RecognizerUtils {
    fun createIntent() =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ru")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 800)
        }

//    override fun parseResult(resultCode: Int, intent: Intent?) =
//        if (resultCode == Activity.RESULT_OK) {
//            intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
//        } else null
//
//
}