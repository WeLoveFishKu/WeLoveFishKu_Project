package id.fishku.fisherseller.seller.data.remote.api.post

import id.fishku.fishersellercore.requests.LoginRequest
import id.fishku.fishersellercore.requests.RegisterRequest
import id.fishku.fishersellercore.response.LoginResponse
import id.fishku.fishersellercore.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("regist")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}