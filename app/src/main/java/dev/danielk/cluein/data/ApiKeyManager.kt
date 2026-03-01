package dev.danielk.cluein.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Android Keystore를 사용해 Gemini API 키를 암호화하여 SharedPreferences에 저장한다.
 * 키 자체는 Keystore 하드웨어에 보관되며, 소스코드·파일·DB에 평문이 저장되지 않는다.
 */
object ApiKeyManager {
    private const val KEY_ALIAS = "cluein_api_key"
    private const val PREFS_NAME = "cluein_secure_prefs"
    private const val PREF_ENCRYPTED_KEY = "enc_api_key"
    private const val PREF_IV = "enc_iv"
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).also { it.load(null) }
        keyStore.getKey(KEY_ALIAS, null)?.let { return it as SecretKey }

        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
        keyGen.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        return keyGen.generateKey()
    }

    fun saveApiKey(context: Context, apiKey: String) {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(apiKey.toByteArray(Charsets.UTF_8))

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(PREF_ENCRYPTED_KEY, Base64.encodeToString(encrypted, Base64.DEFAULT))
            .putString(PREF_IV, Base64.encodeToString(iv, Base64.DEFAULT))
            .apply()
    }

    fun loadApiKey(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encryptedB64 = prefs.getString(PREF_ENCRYPTED_KEY, null) ?: return null
        val ivB64 = prefs.getString(PREF_IV, null) ?: return null

        return try {
            val encrypted = Base64.decode(encryptedB64, Base64.DEFAULT)
            val iv = Base64.decode(ivB64, Base64.DEFAULT)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(128, iv))
            String(cipher.doFinal(encrypted), Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    fun hasApiKey(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .contains(PREF_ENCRYPTED_KEY)

    fun clearApiKey(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .remove(PREF_ENCRYPTED_KEY)
            .remove(PREF_IV)
            .apply()
    }
}
