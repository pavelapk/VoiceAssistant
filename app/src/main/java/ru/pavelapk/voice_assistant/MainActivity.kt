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

    private lateinit var tts: TextToSpeech

    private val textProcessing = SuperNeuralTextProcessing()
    private val recognizer = registerForActivityResult(StartRecognizer()) {
        if (it != null) {
            Toast.makeText(this, it.joinToString(), Toast.LENGTH_SHORT).show()
            val response = textProcessing.process(it[0])
            binding.tvText.text = response
            speak(response)
        } else {
            binding.tvText.text = "error"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                binding.btnSay.isEnabled = true
            } else {
                Toast.makeText(this, "Ошибка TTS", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSay.setOnClickListener {
            speak(binding.tvText.text.toString())
        }

        binding.btnListen.setOnClickListener {
            recognizer.launch()
        }
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
    }
}