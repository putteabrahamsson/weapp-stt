package com.weappstt

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.weappstt.SpeechRecognizerListener

/**
 * Module to expose to RN
 *
 * Ensure that destroy is called when SpeechRecognizer is no longer needed, to clean up resources.
 */
class WeappSttModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext), SpeechRecognizerListener {
    private lateinit var speechRecognizerManager: SpeechRecognizerManager

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

        reactContext.runOnUiQueueThread {
            speechRecognizerManager = SpeechRecognizerManager(reactContext)
            speechRecognizerManager.setSpeechRecognizerListener(this)
        }
    }

    /**
     * Checks if the speech recognizer service is available on the user's device.
     * This can be seen as the first thing to do before starting to listen.
     */
    override fun onAvailable(isAvailable: Boolean) {
        sendEvent("onSpeechAvailable", "$isAvailable")
    }

    /**
     * Callback delivering partial speech recognition results
     *
     * Use this function as the primary way to fetch words and the full text by concatenating the results.
     */
    override fun onPartialResults(partial: String) {
        sendEvent("onSpeechPartialResults", partial)
    }

    /**
     * Callback delivering the final speech recognition result
     * ONLY if setListeningPauseLength or setTotalListeningLength are set to Android default values.
     *
     * This library sets non-default values which means that onResults will return null.
     * Until this bug is resolved, use onPartialResults instead to fetch words.
     *
     * See: https://issuetracker.google.com/issues/227926004
     */
    override fun onResults(final: String) {
        sendEvent("onSpeechResults", final)
    }

    override fun onError(errorCode: String) {
        sendEvent("onSpeechError", "$errorCode")
    }

    /**
     * Helper to send events to RN
     */
    private fun sendEvent(eventName: String, value: String) {
        val params = Arguments.createMap()
        params.putString("value", value)
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }

    /**
     * Functions exposed to RN
     */
    @ReactMethod
    fun startListening() {
        reactContext.runOnUiQueueThread {
            speechRecognizerManager.startListening()
        }
    }

    @ReactMethod
    fun stopListening() {
        reactContext.runOnUiQueueThread {
            speechRecognizerManager.stopListening()
        }
    }

    /**
     * Call on this function when the SpeechRecognizer is no longer used, to release resources.
     * After calling destroy, you must reinitialize if you want to recognize speech again.
     **/
    @ReactMethod
    fun destroy() {
        reactContext.runOnUiQueueThread {
            speechRecognizerManager.destroy()
        }
    }

    @ReactMethod
    fun setLanguage(language: String) {
        reactContext.runOnUiQueueThread {
            speechRecognizerManager.setLanguage(language)
        }
    }

    @ReactMethod
    fun setTotalListeningLength(millis: Int) {
        reactContext.runOnUiQueueThread {
            speechRecognizerManager.setTotalListeningLength(millis)
        }
    }

    @ReactMethod
    fun setListeningPauseLength(millis: Int) {
        reactContext.runOnUiQueueThread {
            speechRecognizerManager.setListeningPauseLength(millis)
        }
    }
}