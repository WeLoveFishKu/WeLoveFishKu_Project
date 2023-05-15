package id.fishku.consumer.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val link_email: String? = null,
    val password: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val token: String? = null,
): Parcelable{
    fun toMap(): Map<String, Any?> =
        mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "token" to token
        )
    fun toMapLinked(): Map<String, Any?> =
        mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "link_email" to link_email,
            "phoneNumber" to phoneNumber,
            "address" to address,
            "token" to token
        )

}