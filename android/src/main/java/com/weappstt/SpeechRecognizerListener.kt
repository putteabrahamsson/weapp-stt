package com.weappstt

interface SpeechRecognizerListener {
    fun onAvailable(isAvailable: Boolean)
    fun onPartialResults(partialTranscript: String)
    fun onResults(transcript: String)
    fun onError(error: String)
}