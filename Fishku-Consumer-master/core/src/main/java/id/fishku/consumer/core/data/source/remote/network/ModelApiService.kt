package id.fishku.consumer.core.data.source.remote.network

import id.fishku.consumer.core.data.source.remote.response.DetectionFishResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ModelApiService {
    @Multipart
    @POST("predict")
    suspend fun uploadImageDetection(
        @Query("ikan") fishName: String?,
        @Part file: MultipartBody.Part
    ): DetectionFishResponse
}