package com.weappstt

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.weappstt.SpeechRecognizerListener

class WeappSttModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {
    private val speechRecognizerManager = SpeechRecognizerManager(reactContext)

    override fun getName(): String {
        return NAME
    }

    companion object {
        const val NAME = "WeappStt"
    }

    // Called when RN initializes this module
    override fun initialize() {
        super.initialize()
        // Attach this module as the manager's listener
        speechRecognizerManager.setSpeechRecognizerListener(this)
    }

    // Implement the interface callbacks:
    override fun onPartialResults(partial: String) {
        sendEvent("onSpeechPartialResults", partial)
    }

    override fun onResults(final: String) {
        sendEvent("onSpeechResults", final)
    }

    override fun onError(errorCode: String) {
        sendEvent("onSpeechError", "$errorCode")
    }

    // Helper to send events to JS
    private fun sendEvent(eventName: String, value: String) {
        val params = Arguments.createMap()
        params.putString("value", value)
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }

    /**
     * Methods exposed to JavaScript
     */
    @ReactMethod
    fun startListening() {
        speechRecognizerManager.startListening()
    }

    @ReactMethod
    fun stopListening() {
        speechRecognizerManager.stopListening()
    }

    @ReactMethod
    fun destroy() {
        speechRecognizerManager.destroy()
    }

    @ReactMethod
    fun setLanguage(language: String) {
        speechRecognizerManager.setLanguage(language)
    }

    @ReactMethod
    fun setTotalListeningLength(millis: Int) {
        speechRecognizerManager.setTotalListeningLength(millis)
    }

    @ReactMethod
    fun setListeningPauseLength(millis: Int) {
        speechRecognizerManager.setListeningPauseLength(millis)
    }
}
