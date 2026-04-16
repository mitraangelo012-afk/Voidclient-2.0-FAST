package com.voidclient.native

import android.util.Log

object NativeInterface {
    private const val TAG = "NativeInterface"
    private var initialized = false

    init {
        try {
            System.loadLibrary("voidclient")
            Log.d(TAG, "Native library loaded successfully")
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Failed to load native library", e)
        }
    }

    external fun nativeInit(): Boolean
    external fun nativeCleanup(): Boolean
    external fun nativeUpdate(): Boolean
    external fun attachToProcess(pid: Int): Boolean
    external fun readMemory(address: Long, size: Int): ByteArray?
    external fun writeMemory(address: Long, data: ByteArray): Boolean
    external fun getEntityList(): Long
    external fun getLocalPlayer(): Long

    fun initialize(): Boolean {
        if (initialized) return true

        try {
            initialized = nativeInit()
            Log.d(TAG, "Native interface initialized: $initialized")
            return initialized
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize native interface", e)
            return false
        }
    }

    fun cleanup(): Boolean {
        if (!initialized) return true

        try {
            val result = nativeCleanup()
            initialized = false
            Log.d(TAG, "Native interface cleaned up: $result")
            return result
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cleanup native interface", e)
            return false
        }
    }

    fun update(): Boolean {
        if (!initialized) return false

        try {
            return nativeUpdate()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update native interface", e)
            return false
        }
    }

    fun isInitialized(): Boolean = initialized
}