package id.fishku.fisherseller.otp.data.remote

import id.fishku.fishersellercore.requests.OtpRequest
import id.fishku.fishersellercore.response.OtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OtpApiService {
    @POST("110033981956213/messages")
    suspend fun sendOtpCode(
        @Body requestBody: OtpRequest
    ): Response<OtpResponse>
}