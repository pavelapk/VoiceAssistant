package ru.pavelapk.voice_assistant

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.pavelapk.voice_assistant.databinding.ActivityMainBinding
import java.util.*
import ru.pavelapk.voice_assistant.StartRecognizer.Companion.launch

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::bind)

    private val recognizer = registerForActivityResult(StartRecognizer()) {
        binding.tvText.text = it?.joinToString() ?: "error"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                binding.btnSay.isEnabled = true
            } else {
                Toast.makeText(this, "Ошибка TTS", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSay.setOnClickListener {
            tts.speak(
                binding.tvText.text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                UUID.randomUUID().toString()
            )
        }

        binding.btnListen.setOnClickListener {
            recognizer.launch()
        }
    }


}