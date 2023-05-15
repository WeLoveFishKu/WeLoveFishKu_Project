package id.fishku.consumer.core.data.source.remote.network

import com.google.gson.JsonObject
import id.fishku.consumer.core.data.source.remote.response.DeliverOrderResponse
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RoketApiService {
    @FormUrlEncoded
    @POST("order")
    suspend fun createDeliveryOrder(
        @Body orderData: JsonObject,
    ): DeliverOrderResponse
}