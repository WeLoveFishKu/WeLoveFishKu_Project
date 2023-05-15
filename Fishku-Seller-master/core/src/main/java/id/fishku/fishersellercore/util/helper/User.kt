package id.fishku.fishersellercore.util.helper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phone_number: String? = null,
    val location: String? = null,
    val roles: String? = null,
    val token: String? = null
): Parcelable
