package id.fishku.consumer.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import id.fishku.consumer.core.R

import javax.inject.Inject


class RemoteConfig @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) {

    companion object{
        const val KEY_VERSION = "update_version_consumer_app"
        const val URI_APP = "market://details?id=id.fishku.consumer"
    }

    fun initRemoteConfig(context: Context){
        remoteConfig.apply {
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            setConfigSettingsAsync(configSettings)

            setDefaultsAsync(R.xml.remote_config_defaults)
            fetchAndActivate().addOnCompleteListener { task ->
                val updated = task.result
                if (task.isSuccessful) {
                    val update = task.result
                    Log.d("TAG",update.toString())
                } else {
                    Log.d("TAG", updated.toString())
                }
            }
        }
        checkForUpdate(context)
    }

    private fun checkForUpdate(context: Context) {

        val appVersion: String = getAppVersion(context)
        val remoteConfig = FirebaseRemoteConfig.getInstance()

        val currentVersion =
            remoteConfig.getString(KEY_VERSION)
        if (!TextUtils.isEmpty(currentVersion) && !TextUtils.isEmpty(appVersion) &&
                getAppVersionWithoutAlphaNumeric(currentVersion) !=
                getAppVersionWithoutAlphaNumeric(appVersion)
        ) {
            onUpdateNeeded(context)
        }
    }

    @Suppress("DEPRECATION")
    private fun getAppVersion(context: Context): String {
        var result: String? = ""
        try {
            result = context.packageManager
                .getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.message?.let { Log.e("TAG", it) }
        }
        return result ?: ""
    }

    private fun getAppVersionWithoutAlphaNumeric(result: String): String {
        return result.replace(".", "")
    }

    private fun onUpdateNeeded(context: Context) {
        val dialogBuilder = AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.title_update))
            .setCancelable(false)
            .setMessage(context.getString(R.string.content_update))
            .setPositiveButton(context.getString(R.string.update))
            { _, _ ->
                openAppOnPlayStore(context)
            }


        dialogBuilder.setNegativeButton(context.getString(R.string.later)) { dialog, _ ->
            dialog?.dismiss()
        }
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()
    }


    private fun openAppOnPlayStore(ctx: Context) {
        val uri = Uri.parse(URI_APP)
        openURI(ctx, uri, ctx.getString(R.string.err_update))
    }
    @SuppressLint("QueryPermissionsNeeded")
    @Suppress("DEPRECATION")
    fun openURI(
        ctx: Context,
        uri: Uri?,
        error_msg: String?
    ) {
        val i = Intent(Intent.ACTION_VIEW, uri)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        if (ctx.packageManager.queryIntentActivities(i, 0).size > 0) {
            ctx.startActivity(i)
        } else if (error_msg != null) {
            Toast.makeText(ctx, error_msg, Toast.LENGTH_SHORT).show()
        }
    }
}