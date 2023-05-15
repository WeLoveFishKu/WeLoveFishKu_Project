package id.fishku.consumer.core.data.source.remote.network

import id.fishku.consumer.core.data.source.remote.request.OtpRequest
import id.fishku.consumer.core.data.source.remote.response.OtpResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface WaApiService {

    @POST("110033981956213/messages")
    suspend fun sendOtpCode(
        @Body requestBody: OtpRequest
    ): OtpResponse
}