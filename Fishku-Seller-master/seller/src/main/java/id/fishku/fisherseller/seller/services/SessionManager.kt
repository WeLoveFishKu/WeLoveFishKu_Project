package id.fishku.fisherseller.seller.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.securepreferences.SecurePreferences
import id.fishku.fishersellercore.util.helper.User

/**
 * Shared prefs
 *
 * @constructor
 *
 * @param context
 */
class SessionManager(context: Context) {
    companion object {
        private const val PREFS_NAME = "prefs_name"
        private const val ID = "id"
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val TOKEN = "token"
        private const val LOGIN = "login"
        private const val PHONE_NUMBER = "phone"
        private const val LOCATION = "location"
        private const val ROLES = "roles"
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
     * Set user
     *
     * @param data
     */
    @SuppressLint("CommitPrefEdits")
    fun setUser(data: User?) {
        val editor = prefs.edit()
        editor.putString(ID, data?.id)
        editor.putString(NAME, data?.name)
        editor.putString(EMAIL, data?.email)
        editor.putString(TOKEN, data?.token)
        editor.putString(PHONE_NUMBER, data?.phone_number)
        editor.putString(LOCATION, data?.location)
        editor.putString(ROLES, data?.roles)
        editor.apply()
    }

    /**
     * Get user
     *
     * @return
     */
    fun getUser(): User =
        User(
            id = prefs.getString(ID, "").toString(),
            name = prefs.getString(NAME, "").toString(),
            email = prefs.getString(EMAIL, "").toString(),
            phone_number = prefs.getString(PHONE_NUMBER, "").toString(),
            location = prefs.getString(LOCATION, "").toString(),
            roles = prefs.getString(ROLES, "").toString(),
            token = prefs.getString(TOKEN, "").toString()
        )


    /**
     * Set login
     *
     * @param value
     */
    fun setLogin(value: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(LOGIN, value)
        editor.apply()
    }

    /**
     * Get login
     *
     * @return
     */
    fun getLogin(): Boolean =
        prefs.getBoolean(LOGIN, false)

    /**
     * Logout
     *
     */
    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}