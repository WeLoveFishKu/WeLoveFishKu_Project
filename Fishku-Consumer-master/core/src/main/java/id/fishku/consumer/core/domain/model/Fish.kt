package id.fishku.consumer.core.domain.model

data class Fish(
    val fishID: Int,
    val name: String,
    val location: String = "",
    val sellerName: String = "",
    val email: String = "",
    val weight: Int = 0,
    val price: Int,
    val photoUrl: String = "",
)