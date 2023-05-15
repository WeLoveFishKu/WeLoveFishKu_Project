package id.fishku.consumer.core.data.source.remote.request

data class NotificationRequest(
    val data: DataNotification,
    val registration_ids: List<String>
)

data class DataNotification(
    val title: String,
    val content: String,
    val consumerEmail: String? = null,
    val chatId: String? = null
)