package id.fishku.fisherseller.seller.data.remote.api.post

import id.fishku.fishersellercore.response.AddResponse
import id.fishku.fishersellercore.response.MessageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PostService {

    @Multipart
    @POST("ikan/input")
    suspend fun postMenu(
        @Part("id_seller") id_seller: RequestBody,
        @Part("name") name: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("photo_url") photo_url: RequestBody,
    ): Response<AddResponse>

    @DELETE("ikan/delete/{id}")
    suspend fun deleteMenu(
        @Path("id") id: String
    ): Response<MessageResponse>

    @Multipart
    @PUT("ikan/edit/{id}")
    suspend fun editMenu(
        @Path("id") id: String,
        @Part("name") name: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
    ): Response<MessageResponse>

    @Multipart
    @POST
    suspend fun uploadImage(
        @Url url: String,
        @Part file: MultipartBody.Part?
    ): Response<MessageResponse>
}