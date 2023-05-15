package id.fishku.fishersellercore.requests

data class NotificationRequest(
    val data: DataNotification,
    val registration_ids: List<String>
)

data class DataNotification(
    val title: String,
    val content: String,
    val sellerEmail: String,
    val chatId: String
)