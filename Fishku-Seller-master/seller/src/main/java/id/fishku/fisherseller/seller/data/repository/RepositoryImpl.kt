package id.fishku.fisherseller.seller.data.repository

import id.fishku.fisherseller.seller.data.datasource.RemoteDataSource
import id.fishku.fisherseller.seller.domain.repository.Repository
import id.fishku.fishersellercore.core.Resource
import id.fishku.fishersellercore.model.MenuModel
import id.fishku.fishersellercore.model.OrderModel
import id.fishku.fishersellercore.requests.AddRequest
import id.fishku.fishersellercore.requests.LoginRequest
import id.fishku.fishersellercore.requests.RegisterRequest
import id.fishku.fishersellercore.response.*
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

/**
 * Repository impl
 *
 * @property data
 * @constructor Create empty Repository impl
 */
class RepositoryImpl @Inject constructor(private val data: RemoteDataSource): Repository {

    override fun register(request: RegisterRequest): Flow<Resource<RegisterResponse>> = data.register(request)
    override fun login(request: LoginRequest): Flow<Resource<LoginResponse>> = data.login(request)
    override fun postMenu(request: AddRequest): Flow<Resource<AddResponse>> = data.postMenu(request)
    override fun getAllFish(idSeller: String): Flow<Resource<GenericResponse<MenuModel>>> = data.getAllFish(idSeller)
    override fun getOrder(idSeller: String): Flow<Resource<GenericResponse<OrderModel>>> = data.getOrder(idSeller)
    override fun getSearchFish(idSeller: String, name: String): Flow<Resource<GenericResponse<MenuModel>>> = data.getSearchFish(idSeller, name)
    override fun getDetailOrder(idOrder: String): Flow<Resource<GetDetailOrderResponse>> = data.getDetailOrder(idOrder)
    override fun deleteMenu(idFish: String): Flow<Resource<MessageResponse>> = data.deleteMenu(idFish)
    override fun editMenu(idFish: String, request: AddRequest): Flow<Resource<MessageResponse>> = data.editMenu(idFish, request)
    override fun uploadImage(url: String, file: File): Flow<Resource<MessageResponse>> = data.uploadImage(url, file)
}