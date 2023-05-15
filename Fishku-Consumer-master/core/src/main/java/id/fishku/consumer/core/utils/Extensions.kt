package id.fishku.consumer.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputLayout
import id.fishku.consumer.core.R
import org.json.JSONObject
import retrofit2.HttpException
import java.sql.Timestamp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun TextInputLayout.showError(isError: Boolean, message: String? = null) {
    if (isError) {
        isErrorEnabled = false
        error = null
        isErrorEnabled = true
        error = message
    } else {
        isErrorEnabled = false
        error = null
    }
}

fun String.showMessage(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun HttpException.getErrorMessage(): String? {
    val response = this.response()?.errorBody()?.string()
    if (response == null || response.isEmpty()) return "Terjadi Kesalahan"
    return try {
        val jsonObject = JSONObject(response)
        jsonObject.getString("message")
    } catch (e: Exception) {
        e.printStackTrace()
        e.message
    }
}

fun Int.convertToRupiah(): String {
    val localID = Locale("in", "ID")
    val formatter = NumberFormat.getCurrencyInstance(localID)
    return formatter.format(this).dropLast(3)
}

fun Int.convertToKilogram(): String = "$this Kg"

fun ImageView.loadFishImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .apply(
            RequestOptions().override(500, 500)
                .placeholder(R.drawable.img_fish_placerholder)
        )
        .centerInside()
        .into(this)
}

fun ImageView.loadUserImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .apply(
            RequestOptions().override(500, 500)
                .placeholder(R.drawable.ic_profile)
        )
        .centerInside()
        .into(this)
}

fun Timestamp.toDateFormat():String{
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(this)
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? {
    return when {
        SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
    }
}

fun String.convertFishName(): String = "Ikan $this"

fun String.addPhotoUrl(): String = "https://storage.fishku.id/photo/$this"