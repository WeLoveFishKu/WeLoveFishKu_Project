package id.fishku.fishersellercore.requests

data class AddRequest(
    val id: String,
    val name: String,
    val weight: String,
    val description: String,
    val price: String,
    val photoUrl: String,
)
