package id.fishku.fishersellercore.data

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.securepreferences.SecurePreferences

/**
 * Shared prefs
 *
 * @constructor
 *
 * @param context
 */
class LocalData(context: Context) {
    companion object {
        private const val PREFS_NAME = "local_data"
        private const val CODE_OTP = "code_otp"
        private const val NUMBER = "number"
        private const val TOKEN_FCM = "token_fcm"
    }

    private var prefs: SharedPreferences = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val spec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        val masterKey = MasterKey.Builder(context)
            .setKeyGenParameterSpec(spec)
            .build()
        EncryptedSharedPreferences.create(
            context, PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } else {
        SecurePreferences(context)
    }


    /**
     * Set login
     *
     * @param value
     */
    fun setCodeOtp(value: Int) {
        val editor = prefs.edit()
        editor.putInt(CODE_OTP, value)
        editor.apply()
    }

    fun setTokenFcm(value: String) {
        val editor = prefs.edit()
        editor.putString(TOKEN_FCM, value)
        editor.apply()
    }

    fun getTokenFcm(): String? =
        prefs.getString(TOKEN_FCM, "")

    /**
     * Get login
     *
     * @return
     */
    fun getCodeOtp(): Int =
        prefs.getInt(CODE_OTP, 0)

    fun setNumber(value: String?) {
        val editor = prefs.edit()
        editor.putString(NUMBER, value)
        editor.apply()
    }

    /**
     * Get login
     *
     * @return
     */
    fun getNumber(): String? =
        prefs.getString(NUMBER, "")
}