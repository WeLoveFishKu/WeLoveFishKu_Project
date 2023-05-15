package id.fishku.fisherseller.seller.data.remote.api.get

import id.fishku.fishersellercore.model.MenuModel
import id.fishku.fishersellercore.model.OrderModel
import id.fishku.fishersellercore.response.GenericResponse
import id.fishku.fishersellercore.response.GetDetailOrderResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GetService {
    @GET("ikan/{id}")
    suspend fun getAllFish(
        @Path("id") id: String
    ): Response<GenericResponse<MenuModel>>

    @GET("ikan/{id}/{name}")
    suspend fun getSearchFish(
        @Path("id") id: String,
        @Path("name") name: String
    ): Response<GenericResponse<MenuModel>>

    @GET("pesanan/{id}")
    suspend fun getAllOrder(
        @Path("id") id: String
    ): Response<GenericResponse<OrderModel>>

    @GET("pesanan/detail/{id}")
    suspend fun getDetailOrder(
        @Path("id") id: String
    ): Response<GetDetailOrderResponse>
}