package id.fishku.consumer.core.domain.model

data class Login(
    val message: String? = null,
    val token: String? = null,
    val user: User? = null,
)