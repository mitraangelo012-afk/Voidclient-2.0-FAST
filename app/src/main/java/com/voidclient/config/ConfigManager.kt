package com.voidclient.config

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ConfigManager(private val context: Context) {
    companion object {
        private const val TAG = "ConfigManager"
        private const val CONFIG_FILE = "voidclient_config.json"
    }

    private val config = JSONObject()

    init {
        loadConfig()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (config.has(key)) {
            config.getBoolean(key)
        } else {
            defaultValue
        }
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return if (config.has(key)) {
            config.getInt(key)
        } else {
            defaultValue
        }
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        return if (config.has(key)) {
            config.getDouble(key).toFloat()
        } else {
            defaultValue
        }
    }

    fun getString(key: String, defaultValue: String): String {
        return if (config.has(key)) {
            config.getString(key)
        } else {
            defaultValue
        }
    }

    fun setBoolean(key: String, value: Boolean) {
        config.put(key, value)
        saveConfig()
    }

    fun setInt(key: String, value: Int) {
        config.put(key, value)
        saveConfig()
    }

    fun setFloat(key: String, value: Float) {
        config.put(key, value.toDouble())
        saveConfig()
    }

    fun setString(key: String, value: String) {
        config.put(key, value)
        saveConfig()
    }

    private fun loadConfig() {
        try {
            val file = File(context.filesDir, CONFIG_FILE)
            if (!file.exists()) {
                Log.d(TAG, "Config file does not exist, using defaults")
                return
            }

            val buffer = ByteArray(file.length().toInt())
            FileInputStream(file).use { fis ->
                fis.read(buffer)
            }

            val json = String(buffer)
            config = JSONObject(json)
            Log.d(TAG, "Config loaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load config", e)
        }
    }

    private fun saveConfig() {
        try {
            val file = File(context.filesDir, CONFIG_FILE)
            FileOutputStream(file).use { fos ->
                fos.write(config.toString(2).toByteArray())
            }
            Log.d(TAG, "Config saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save config", e)
        }
    }

    fun resetToDefaults() {
        config.put("esp_enabled", true)
        config.put("esp_box", true)
        config.put("esp_tracer", false)
        config.put("esp_health", true)
        config.put("aim_assist_enabled", true)
        config.put("aim_assist_fov", 90.0)
        config.put("aim_assist_smooth", 2.0)
        config.put("overlay_alpha", 0.7)
        saveConfig()
    }
}