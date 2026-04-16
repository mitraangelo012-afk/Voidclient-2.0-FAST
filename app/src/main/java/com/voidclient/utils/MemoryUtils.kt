package com.voidclient.utils

import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

object MemoryUtils {
    private const val TAG = "MemoryUtils"

    fun byteArrayToLong(bytes: ByteArray, offset: Int = 0): Long {
        if (bytes.size < offset + 8) {
            Log.e(TAG, "Not enough bytes to read long")
            return 0L
        }

        return ByteBuffer.wrap(bytes, offset, 8)
            .order(ByteOrder.LITTLE_ENDIAN)
            .getLong()
    }

    fun byteArrayToInt(bytes: ByteArray, offset: Int = 0): Int {
        if (bytes.size < offset + 4) {
            Log.e(TAG, "Not enough bytes to read int")
            return 0
        }

        return ByteBuffer.wrap(bytes, offset, 4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .getInt()
    }

    fun byteArrayToFloat(bytes: ByteArray, offset: Int = 0): Float {
        if (bytes.size < offset + 4) {
            Log.e(TAG, "Not enough bytes to read float")
            return 0f
        }

        return ByteBuffer.wrap(bytes, offset, 4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .getFloat()
    }

    fun longToByteArray(value: Long): ByteArray {
        return ByteBuffer.allocate(8)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putLong(value)
            .array()
    }

    fun intToByteArray(value: Int): ByteArray {
        return ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(value)
            .array()
    }

    fun floatToByteArray(value: Float): ByteArray {
        return ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putFloat(value)
            .array()
    }

    fun findPattern(memory: ByteArray, pattern: String): Int {
        // Convert pattern string to byte array
        // Pattern format: "AA BB ?? CC" where ?? is wildcard
        val patternBytes = pattern.split(" ").map {
            if (it == "??" || it == "?") null else it.toInt(16).toByte()
        }.toTypedArray()

        // Search for pattern in memory
        for (i in 0..memory.size - patternBytes.size) {
            var found = true
            for (j in patternBytes.indices) {
                if (patternBytes[j] != null && memory[i + j] != patternBytes[j]) {
                    found = false
                    break
                }
            }
            if (found) {
                return i
            }
        }

        return -1 // Not found
    }
}