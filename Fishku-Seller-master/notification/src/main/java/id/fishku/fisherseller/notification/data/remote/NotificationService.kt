package id.fishku.fisherseller.notification.data.remote

import id.fishku.fishersellercore.requests.NotificationRequest
import id.fishku.fishersellercore.response.NotificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationService {
    @POST("fcm/send")
    suspend fun pushNotification(
        @Body notification: NotificationRequest
    ): Response<NotificationResponse>
}