package ru.pavelapk.voice_assistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.pavelapk.voice_assistant.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::bind)

    private lateinit var tts: TextToSpeech
    private lateinit var recognizer: SpeechRecognizer

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "спасибо за содействие", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "отдай права", Toast.LENGTH_SHORT).show()
            }
        }

    private val textProcessing = SuperNeuralTextProcessing()

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(p0: Bundle?) {
            setLoudness(0f)
            binding.tvMyText.text = ""
            binding.tvAssistantText.text = "Слушаю..."
        }

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rms: Float) {
//            Log.d("dadaya", "onRmsChanged: $rms")
            setLoudness(rms + 2f)
        }

        override fun onBufferReceived(p0: ByteArray?) {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            setLoudness(0f)
            val text = when (error) {
                SpeechRecognizer.ERROR_NO_MATCH -> {
                    "Вас не слышно"
                }
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT, SpeechRecognizer.ERROR_NETWORK -> {
                    "Проблемы с интернет соединением"
                }
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                    "Говорите после сигнала"
                }
                SpeechRecognizer.ERROR_SERVER -> {
                    "Отсутствует интернет соединением"
                }
                else -> "Неизвестная ошибка"
            }
            binding.tvAssistantText.text = text
            speak(text)

        }

        override fun onResults(results: Bundle?) {
            setLoudness(0f)
            if (results != null) {
                for (key in results.keySet()) {
                    Log.d("dadaya", "$key = '${results.get(key)}'")
                }
                val text =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0) ?: ""
                binding.tvMyText.text = text
                val response = textProcessing.process(text)
                binding.tvAssistantText.text = response
                speak(response, true)
            }
        }

        override fun onPartialResults(partialResult: Bundle?) {
            if (partialResult != null) {
                for (key in partialResult.keySet()) {
                    Log.d("dadaya", "$key = '${partialResult.get(key)}'")
                }
                val text =
                    partialResult.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
                        ?: ""
                binding.tvMyText.text = text
            }
        }

        override fun onEvent(p0: Int, p1: Bundle?) {}
    }

    private val ttsListener = object : UtteranceProgressListener() {
        override fun onError(utteranceId: String?) {}
        override fun onStart(utteranceId: String?) {}

        override fun onDone(utteranceId: String?) {
            Log.d("dadaya", "onDoneTTS: $utteranceId")
            if (utteranceId?.startsWith("1_") == true) runOnUiThread { listen() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.addEarcon("[frog]", packageName, R.raw.frog)
                tts.setOnUtteranceProgressListener(ttsListener)
                binding.btnSay.isEnabled = true
            } else {
                Toast.makeText(this, "Ошибка TTS", Toast.LENGTH_SHORT).show()
            }
        }
        checkPermissions()
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizer.setRecognitionListener(recognitionListener)

        binding.btnSay.setOnClickListener {
            speak(binding.tvAssistantText.text.toString())
        }

        binding.btnListen.setOnClickListener {
            listen()
        }
    }

    private fun checkPermissions(): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                return true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                Toast.makeText(this, "отдай права", Toast.LENGTH_SHORT).show()
                requestPermission.launch(Manifest.permission.RECORD_AUDIO)
                return false
            }
            else -> {
                requestPermission.launch(Manifest.permission.RECORD_AUDIO)
                return false
            }
        }
    }

    private fun listen() {
        if (checkPermissions())
            recognizer.startListening(RecognizerUtils.createIntent())
    }

    private fun speak(text: String, recognitionAfterSpeech: Boolean = false) {
        val uid = (if (recognitionAfterSpeech) "1_" else "0_") + UUID.randomUUID().toString()
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, uid)
//        tts.playEarcon("[frog]", TextToSpeech.QUEUE_ADD, null, UUID.randomUUID().toString())
    }

    private fun setLoudness(loudness: Float) {
        binding.viewLoudness.scaleX = loudness
        binding.viewLoudness.scaleY = loudness
    }
}