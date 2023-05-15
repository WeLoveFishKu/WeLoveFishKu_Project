package id.fishku.consumer.core.data.source.local.datastore

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.securepreferences.SecurePreferences
import id.fishku.consumer.core.domain.model.User

/**
 * Shared prefs
 *
 * @constructor
 *
 * @param context
 */

class LocalData(context: Context) {


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

    @SuppressLint("CommitPrefEdits")
    fun setDataUser(value: User?) {
        val editor = prefs.edit()
        editor.apply {
            putInt(ID, value?.id ?: 0)
            putString(NAME, value?.name)
            putString(EMAIL, value?.email)
            putString(NUMBER_FOR_OTP, value?.phoneNumber)
            putString(ADDRESS, value?.address)
            putString(TOKEN, value?.token)
        }
        editor.apply()
    }

    fun getDataUser(): User =
        User(
            id = prefs.getInt(ID, 0),
            name = prefs.getString(NAME, ""),
            email = prefs.getString(EMAIL, ""),
            phoneNumber = prefs.getString(NUMBER_FOR_OTP, ""),
            address = prefs.getString(ADDRESS, ""),
            token = prefs.getString(TOKEN, "")
        )

    @SuppressLint("CommitPrefEdits")
    fun setStateAuth(value: Boolean = false){
        val editor = prefs.edit()
        editor.putBoolean(IS_AUTH, value)
        editor.apply()
    }

    fun getStateAuth(): Boolean =
        prefs.getBoolean(IS_AUTH, false)


    /**
     * Get login
     *
     * @return
     */
    fun getCodeOtp(): Int =
        prefs.getInt(CODE_OTP, 0)

    fun setNumber(value: String?) {
        val editor = prefs.edit()
        editor.putString(NUMBER_FOR_OTP, value)
        editor.apply()
    }

    /**
     * Get login
     *
     * @return
     */
    fun getNumber(): String? =
        prefs.getString(NUMBER_FOR_OTP, "")

    fun setTokenFcm(value: String) {
        val editor = prefs.edit()
        editor.putString(TOKEN_FCM, value)
        editor.apply()
    }

    fun getTokenFcm(): String? =
        prefs.getString(TOKEN_FCM, "")

    companion object{
        private const val PREFS_NAME = "local_data"
        private const val CODE_OTP = "code_otp"
        private const val NUMBER_FOR_OTP = "number"

        private const val ID = "id"
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val IS_AUTH = "auth"
        private const val ADDRESS = "address"
        private const val TOKEN = "token_auth"

        private const val TOKEN_FCM = "token_fcm_consumer"

    }
}