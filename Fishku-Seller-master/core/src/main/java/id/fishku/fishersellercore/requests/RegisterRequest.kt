package id.fishku.fishersellercore.requests

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone_number: String,
    val location: String,
    val roles: String
)
