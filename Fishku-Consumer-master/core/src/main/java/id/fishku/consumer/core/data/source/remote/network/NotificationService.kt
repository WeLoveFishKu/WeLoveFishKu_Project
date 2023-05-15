package id.fishku.consumer.core.data.source.remote.network

import id.fishku.consumer.core.data.source.remote.request.NotificationRequest
import id.fishku.consumer.core.data.source.remote.response.NotificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationService {
    @POST("fcm/send")
    suspend fun pushNotification(
        @Body notification: NotificationRequest
    ): NotificationResponse
}