package com.weappstt

interface SpeechRecognizerListener {
    fun onPartialResults(partialTranscript: String)
    fun onResults(transcript: String)
    fun onError(error: String)
}