package id.fishku.fishersellercore.model

data class UserModel(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val link_email: String? = null,
    val phone_number: String? = null,
    val location: String? = null,
    val roles: String? = null,
    val token: String? = null
){
    fun toMap(): Map<String, Any?> =
        mapOf(
            "id" to id?.toInt(),
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
            "phone_number" to phone_number,
            "location" to location,
            "roles" to roles,
            "token" to token
        )
}
