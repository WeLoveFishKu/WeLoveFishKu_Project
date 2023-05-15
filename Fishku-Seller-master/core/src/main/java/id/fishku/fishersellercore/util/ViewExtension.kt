package id.fishku.fishersellercore.util

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import id.fishku.fishersellercore.R
import id.fishku.fishersellercore.R.color.red_error
import java.text.DateFormatSymbols
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Set image
 *
 * @param url
 */
fun ImageView.setImage(url: String) =
    Glide.with(this)
        .load(url)
        .into(this)

/**
 * Text trim
 *
 */
fun EditText.textTrim() = this.text.toString().trim()

/**
 * With color
 *
 * @param colorInt
 * @return
 */
fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar{
    this.view.setBackgroundColor(colorInt)
    return this
}

/**
 * My snack bar
 *
 * @param msg
 * @param id
 */
@SuppressLint("ResourceAsColor")
fun View.mySnackBar(msg: String, @ColorRes id: Int? = null) {

    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT)
        .withColor(resources.getColor(id ?: red_error))
        .show()
}

/**
 * Is valid email
 *
 * @return
 */
fun String.isValidEmail(): Boolean =
    this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

/**
 * After text changed
 *
 * @param afterTextChanged
 * @receiver
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    })
}

/**
 * Validate
 *
 * @param message
 * @param validator
 * @receiver
 */
fun EditText.validate(message: String, validator: (String) -> Boolean) {
    this.afterTextChanged {
        this.error = if (validator(it)) null else message
    }
    this.error = if (validator(this.text.toString())) null else message
}

/**
 * Clear error
 *
 */
fun EditText.clearError() {
    error = null
}

/**
 * Hide keyboard
 *
 */
fun View.hideKeyboard() {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * To start capitalize
 *
 * @return
 */
fun String.toStartCapitalize(): String =
    this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }


/**
 * Convert date
 *
 * @param context
 * @return
 */
@SuppressLint("SimpleDateFormat", "ResourceType")
fun String.convertDate(context: Context): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val date = dateFormat.parse(this)
    val codeDate = dateFormat.format(date!!)
    val splitDate = codeDate.split("-")
    val monthName = DateFormatSymbols().months[splitDate[1].toInt() - 1]
    val cal = Calendar.getInstance()
    cal.time = date
    val week = cal[Calendar.DAY_OF_WEEK]
    val dayName = DateFormatSymbols().weekdays[week]

    val listDay = mapOf(
        context.resources.getStringArray(R.array.sunday)[0] to context.resources.getStringArray(R.array.sunday)[1],
        context.resources.getStringArray(R.array.monday)[0] to context.resources.getStringArray(R.array.monday)[1],
        context.resources.getStringArray(R.array.tuesday)[0] to context.resources.getStringArray(R.array.tuesday)[1],
        context.resources.getStringArray(R.array.wednesday)[0] to context.resources.getStringArray(R.array.wednesday)[1],
        context.resources.getStringArray(R.array.thursday)[0] to context.resources.getStringArray(R.array.thursday)[1],
        context.resources.getStringArray(R.array.friday)[0] to context.resources.getStringArray(R.array.friday)[1],
        context.resources.getStringArray(R.array.saturday)[0] to context.resources.getStringArray(R.array.saturday)[1],
    )
    var day: String? = null
    listDay.asSequence().filter { it.key == dayName }.forEach {
        day = it.value
    }
    return context.resources.getString(R.string.date_convert, day,splitDate[2], monthName, splitDate[0])
}

/**
 * Convert currency format
 *
 * @return
 */
@SuppressLint("StringFormatMatches")
fun String.convertCurrencyFormat(): String {
    val formatter: NumberFormat = DecimalFormat("#,###")

    return formatter.format(toInt())
}
