package com.example.fittracker.data

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * PBKDF2 with HMAC-SHA256, 100_000 iterations, 16-byte salt, 32-byte hash.
 * Stored salt + hash are base64 strings.
 */
object PasswordHasher {

    private const val ITERATIONS = 100_000
    private const val KEY_LENGTH = 256
    private const val SALT_BYTES = 16

    fun hash(password: String): HashResult {
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = pbkdf2(password.toCharArray(), salt)
        return HashResult(
            saltBase64 = android.util.Base64.encodeToString(salt, android.util.Base64.NO_WRAP),
            hashBase64 = android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP),
        )
    }

    fun verify(password: String, saltBase64: String, hashBase64: String): Boolean {
        if (saltBase64.isEmpty() || hashBase64.isEmpty()) return false
        val salt = android.util.Base64.decode(saltBase64, android.util.Base64.NO_WRAP)
        val expected = android.util.Base64.decode(hashBase64, android.util.Base64.NO_WRAP)
        val actual = pbkdf2(password.toCharArray(), salt)
        // Constant-time comparison
        if (actual.size != expected.size) return false
        var diff = 0
        for (i in actual.indices) diff = diff or (actual[i].toInt() xor expected[i].toInt())
        return diff == 0
    }

    private fun pbkdf2(password: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return skf.generateSecret(spec).encoded
    }

    data class HashResult(val saltBase64: String, val hashBase64: String)
}
