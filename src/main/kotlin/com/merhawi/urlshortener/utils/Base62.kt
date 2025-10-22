package com.merhawi.urlshortener.utils

import java.math.BigInteger
import java.nio.ByteBuffer

/**
 * Base62 encoder with configurable alphabet.
 * Used for converting byte arrays to compact URL-safe strings.
 */
object Base62 {

    fun encode(bytes: ByteArray, bits: Int, alphabet: String): String {
        val totalBytes = (bits + 7) / 8
        val buffer = ByteBuffer.allocate(totalBytes + 1)
        buffer.put(0)
        buffer.put(bytes.copyOf(totalBytes))
        val mag = buffer.array()

        var value = BigInteger(mag)
        val shift = (totalBytes * 8) - bits
        if (shift > 0) value = value.shiftRight(shift)

        val base = BigInteger.valueOf(alphabet.length.toLong())
        val sb = StringBuilder()

        if (value == BigInteger.ZERO) sb.append(alphabet[0])
        while (value > BigInteger.ZERO) {
            val (div, rem) = value.divideAndRemainder(base)
            sb.append(alphabet[rem.toInt()])
            value = div
        }

        return sb.reverse().toString()
    }
}
