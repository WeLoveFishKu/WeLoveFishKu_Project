package id.fishku.consumer.core.data.source.remote.network

import com.google.gson.JsonObject
import id.fishku.consumer.core.data.source.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface MainApiService {
    @FormUrlEncoded
    @POST("consumer/login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    @FormUrlEncoded
    @POST("consumer/regist")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone_number") phoneNumber: String,
        @Field("address") address: String,
    ): RegisterResponse

    @GET("consumer/ikan/all")
    suspend fun getAllFish(): FishResponse

    @GET("consumer/ikan/{namaIkan}")
    suspend fun searchFishes(
        @Path("namaIkan") query: String,
    ): FishResponse

    @GET("consumer/ikan/detail/{idIkan}")
    suspend fun getDetilFish(
        @Path("idIkan") fishID: Int,
    ): DetailFishResponse

    @GET("consumer/keranjang/{idConsumer}")
    suspend fun getCart(
        @Path("idConsumer") consumerID: Int,
    ): CartResponse

    @FormUrlEncoded
    @PUT("consumer/keranjang/edit/weight/{idCart}")
    suspend fun editCart(
        @Path("idCart") cartID: Int,
        @Field("weight") newWeight: Int,
    ): EditCartResponse

    @DELETE("consumer/keranjang/delete/{idCart}")
    suspend fun deleteCart(
        @Path("idCart") cartID: Int,
    ): DeleteCartResponse

    @FormUrlEncoded
    @POST("consumer/keranjang/input")
    suspend fun inputCart(
        @Field("id_fish") fishID: Int,
        @Field("id_consumer") consumerID: Int,
        @Field("notes") notes: String,
        @Field("weight") weight: Int,
    ): InputCartResponse

    @GET("consumer/pesanan/all/idConsumer/{idConsumer}")
    suspend fun getAllOrderByIdConsumer(
        @Path("idConsumer") consumerID: Int,
    ): OrderResponse

    @GET("consumer/pesanan/{idConsumer}/{idOrdering}")
    suspend fun getOrderDetail(
        @Path("idConsumer") consumerID: Int,
        @Path("idOrdering") orderingID: Int,
    ): OrderDetailResponse

    @FormUrlEncoded
    @POST("consumer/pesanan/input")
    suspend fun inputOrder(
        @Field("date") date: String,
        @Field("notes") notes: String,
        @Field("status") status: String,
        @Field("kurir") courier: String,
        @Field("latitude") lat: String,
        @Field("longitude") long: String,
        @Field("alamat") address: String,
        @Field("invoice_url") invoiceUrl: String,
    ): InputOrderResponse

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("consumer/pesanan/input/detail/{idConsumer}")
    suspend fun inputOrderDetail(
        @Path("idConsumer") cunsomerID: Int,
        @Body orderData: JsonObject,
    ): InputOrderDetailResponse

    @GET("consumer/detect/fish/")
    suspend fun getListFishDetection(): ListFishDetectionResponse

    @FormUrlEncoded
    @POST("payment/create/invoice")
    suspend fun createInvoice(
        @Field("externalID") externalID: String,
        @Field("payerEmail") payerEmail: String,
        @Field("description") description: String,
        @Field("amount") amount: Int,
    ): InvoiceResponse

    @Multipart
    @PUT("consumer/profile/edit/{idConsumer}")
    suspend fun editProfile(
        @Path("idConsumer") consumerID: Int,
        @Part("name") name: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("address") address: RequestBody,
        @Part file: MultipartBody.Part,
    ): EditProfileResponse
}