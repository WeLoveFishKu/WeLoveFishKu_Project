package id.fishku.fishersellercore.util

import android.Manifest

object Constants {
    const val UNKNOWN_ERROR = "Unknown error occurred, please try again later."
    const val TIMEOUT_ERROR = "The server took too long to respond, please try again later."

    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    const val REQUEST_CODE_PERMISSIONS = 10

    const val SPLASH_DURATION: Long = 1000

    const val SIMPLE_DATE_FORMAT = "dd-MMM-yyyy"
    const val SUFFIX_IMG = ".jpg"
    const val IMAGE_TYPE = "image/*"

    const val URL_IMAGE = "https://storage.fishku.id/photo/"

    const val SEND_ORDER_TO_DETAIL = "send_order_to_detail"
    const val SEND_MENU_TO_EDIT = "send_menu_to_edit"

    const val IMAGE_API = "https://storage.fishku.id/upload/photo"

    const val TIME_IN_MILLIS = 1000L
    const val MIN_SCROLL_POSITION = 2

    // otp const
    val RANGE_CODE_FIRST = 10000 .. 100000
    val RANGE_CODE_SECOND = 20000 .. 120000
    const val WAITING_MINUTES = 60
    const val DELAY_SECONDS = 1000L
    const val COUNTRY_CODE = "62"

    const val DELAY_LOGOUT = 500L
}