package com.weappstt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.weappstt.SpeechRecognizerListener
import kotlin.apply
import kotlin.collections.forEach

class SpeechRecognizerManager(private val context: Context) {
    private var speechRecognizerListener: SpeechRecognizerListener? = null

    fun setSpeechRecognizerListener(listener: SpeechRecognizerListener) {
        this.speechRecognizerListener = listener
    }

    // Default language
    private val swedishLanguage = "sv-SE"

    // Define the intent with the necessary extras.
    private val recognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, swedishLanguage)
        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 60000)
    }

    private var partialTranscript = ""

    // Initialize SpeechRecognizer if available.
    private val speechRecognizer: SpeechRecognizer? =
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            Log.e("SpeechRecognizer", "Speech recognition service NOT available")
            speechRecognizerListener?.onError("Speech recognition service NOT available")
            null
        }

    init {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {
                Log.d("SpeechRecognizer", "Ready for speech")
                // Ensures a clean transcript when starting.
                partialTranscript = ""
            }

            override fun onBeginningOfSpeech() {
                Log.d("SpeechRecognizer", "onBeginningOfSpeech")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Optional: handle RMS changes for visual feedback.
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d("SpeechRecognizer", "onEndOfSpeech")
            }

            override fun onError(error: Int) {
                Log.e("SpeechRecognizer", "onError: $error")
                val errorMessage = handleErrorCode(error)
                speechRecognizerListener?.onError("$errorMessage Error code: $error")
            }

            /**
             * If non-default settings are used onResults will return null, use onPartialResults instead to fetch words.
             * Refer to this: https://issuetracker.google.com/issues/227926004
             */
            override fun onResults(results: Bundle?) {
                val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.d("SpeechRecognizer", "Results: $data")

                speechRecognizerListener?.onResults(data.toString())
            }

            /**
             * Use if setListeningPauseLength or setTotalListeningLength are used.
             */
            override fun onPartialResults(partialResults: Bundle?) {
                val partialData =
                    partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                partialData?.forEach { word ->
                    Log.d("SpeechRecognizer", "Partial: $word")
                    partialTranscript += "$word "
                    speechRecognizerListener?.onPartialResults("$word ")
                }
                // Optionally, notify a listener or update an observable property.
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun handleErrorCode(error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "ERROR_AUDIO"
            SpeechRecognizer.ERROR_CLIENT -> "ERROR_CLIENT"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "ERROR_INSUFFICIENT_PERMISSIONS"
            SpeechRecognizer.ERROR_NETWORK -> "ERROR_NETWORK"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "ERROR_NETWORK_TIMEOUT"
            SpeechRecognizer.ERROR_NO_MATCH -> "ERROR_NO_MATCH"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "ERROR_RECOGNIZER_BUSY"
            SpeechRecognizer.ERROR_SERVER -> "ERROR_SERVER"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "ERROR_SPEECH_TIMEOUT"
            else -> ""
        }
    }

    fun setLanguage(language: String) {
        // Update the language in the recognizer intent.
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
    }

    /**
     * Sets the length for how long the recording should remain active.
     */
    fun setTotalListeningLength(millis: Int) {
        // Update the listening length in the recognizer intent.
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
            millis
        )
    }

    /**
     * Sets the length for how long a pause in speech can occur before the recording is stopped.
     */
    fun setListeningPauseLength(millis: Int) {
        // Update the maximum pause length in the recognizer intent.
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
            millis
        )
    }

    fun startListening() {
        Log.d("SpeechRecognizer", "Starting to listen...")
        speechRecognizer?.startListening(recognizerIntent)
    }

    fun stopListening() {
        Log.d("SpeechRecognizer", "Stopping to listen...")
        speechRecognizer?.stopListening()
    }

    fun destroy() {
        Log.d("SpeechRecognizer", "Destroying...")
        speechRecognizer?.destroy()
    }
}