package com.rubyhuntersky.seismic_stampede

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


object KeyMaster {

    private class PasswordItem(val password: ByteArray, val id: Int)

    private var item: PasswordItem? = null

    fun setPassword(password: CharArray): Int {
        return SecureRandom().nextInt()
            .also { item = PasswordItem(copyAndZero(password), it) }
    }

    fun decrypt(cipherItem: CipherItem, passwordId: Int): ByteArray {
        require(passwordId == item!!.id)
        val parts = cipherItem.name.split(':')
        check(parts.size == 2)
        val nonce = parts[0].hexToByteArray()
        val key = deriveKey(nonce, item!!.password)
        val iv = parts[1].hexToByteArray()
        return plainBytes(cipherItem.cipherBytes, key, iv).also { key.zero() }
    }

    fun encryptAndZeroPlain(plainBytes: ByteArray, passwordId: Int): CipherItem {
        require(passwordId == item!!.id)
        val nonce = ByteArray(HMAC_NONCE_LENGTH).also { SecureRandom().nextBytes(it) }
        val key = deriveKey(nonce, item!!.password)
        check(key.size == 32)
        val iv = ByteArray(IV_LENGTH).also { SecureRandom().nextBytes(it) }
        return CipherItem(
            name = cipherName(nonce, iv),
            cipherBytes = cipherBytes(plainBytes, key, iv)
        ).also {
            plainBytes.zero()
            key.zero()
        }
    }

    private fun plainBytes(
        cipherBytes: ByteArray,
        key: ByteArray,
        iv: ByteArray
    ): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(key, "AES")
        val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec)
        return cipher.doFinal(cipherBytes)
    }

    private fun cipherBytes(
        plainBytes: ByteArray,
        key: ByteArray,
        iv: ByteArray
    ): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = SecretKeySpec(key, "AES")
        val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec)
        return cipher.doFinal(plainBytes)
    }

    private fun deriveKey(nonce: ByteArray, password: ByteArray): ByteArray {
        val sha256Hmac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(nonce, "HmacSHA256")
        sha256Hmac.init(secretKey)
        return sha256Hmac.doFinal(password)!!
    }

    private fun copyAndZero(chars: CharArray): ByteArray {
        return ByteArray(chars.size).also { bytes ->
            chars.indices.forEach { i ->
                bytes[i] = chars[i].toByte()
                chars[i] = 'x'
            }
        }
    }

    private fun cipherName(nonce: ByteArray, iv: ByteArray): String =
        "${nonce.toHex()}:${iv.toHex()}"

    private const val HMAC_NONCE_LENGTH = 16
    private const val GCM_TAG_LENGTH = 16
    private const val IV_LENGTH = 16
}

