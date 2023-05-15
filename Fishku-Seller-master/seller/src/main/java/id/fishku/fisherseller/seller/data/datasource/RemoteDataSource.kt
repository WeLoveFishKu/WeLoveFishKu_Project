package id.fishku.fisherseller.seller.data.datasource

import id.fishku.fisherseller.seller.data.remote.api.get.GetService
import id.fishku.fisherseller.seller.data.remote.api.post.PostService
import id.fishku.fisherseller.seller.data.remote.api.post.UserService
import id.fishku.fishersellercore.core.ErrorParser
import id.fishku.fishersellercore.core.Resource
import id.fishku.fishersellercore.core.SafeCall
import id.fishku.fishersellercore.model.MenuModel
import id.fishku.fishersellercore.model.OrderModel
import id.fishku.fishersellercore.requests.AddRequest
import id.fishku.fishersellercore.requests.LoginRequest
import id.fishku.fishersellercore.requests.RegisterRequest
import id.fishku.fishersellercore.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

/**
 * Data source
 *
 * @property safeCall
 * @property converter
 * @property userService
 * @property postService
 * @property getService
 * @constructor Create empty Data source
 */
class RemoteDataSource @Inject constructor(
    private val safeCall: SafeCall,
    private val converter: ErrorParser,
    private val userService: UserService,
    private val postService: PostService,
    private val getService: GetService
){
    /**
     * Register
     *
     * @param request
     * @return
     */
    fun register(request: RegisterRequest): Flow<Resource<RegisterResponse>> = flow {
        emit(Resource.loading(null))
        val res = safeCall.enqueue(request, converter::converterMessageError, userService::register)
        emit(res)
    }.flowOn(Dispatchers.IO)

    /**
     * Login
     *
     * @param request
     * @return
     */
    fun login(request: LoginRequest): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.loading(null))

        val res = safeCall.enqueue(request, converter::converterMessageError, userService::login)
        emit(res)
    }.flowOn(Dispatchers.IO)

    /**
     * Post menu
     *
     * @param request
     * @return
     */
    fun postMenu(request: AddRequest): Flow<Resource<AddResponse>> = flow {
        emit(Resource.loading(null))

        val id = request.id.toRequestBody("text/plain".toMediaType())
        val name = request.name.toRequestBody("text/plain".toMediaType())
        val weight = request.weight.toRequestBody("text/plain".toMediaType())
        val desc = request.description.toRequestBody("text/plain".toMediaType())
        val price = request.price.toRequestBody("text/plain".toMediaType())
        val photoUrl = request.photoUrl.toRequestBody("text/plain".toMediaType())
        val res = safeCall.enqueueAdd(id, name, weight, desc, price, photoUrl, converter::converterAddError, postService::postMenu)
        emit(res)
    }.flowOn(Dispatchers.IO)

    /**
     * Get all fish
     *
     * @param idSeller
     * @return
     */
    fun getAllFish(idSeller: String): Flow<Resource<GenericResponse<MenuModel>>> = flow {
        emit(Resource.loading(null))

        val res = safeCall.enqueueA(idSeller, converter::converterGenericError, getService::getAllFish)
        emit(res)

    }.flowOn(Dispatchers.IO)

    /**
     * Get search fish
     *
     * @param idSeller
     * @param name
     * @return
     */
    fun getSearchFish(idSeller: String, name: String): Flow<Resource<GenericResponse<MenuModel>>> = flow {
        emit(Resource.loading(null))

        val res = safeCall.enqueue(idSeller, name, converter::converterGenericError, getService::getSearchFish)
        emit(res)

    }.flowOn(Dispatchers.IO)

    /**
     * Get order
     *
     * @param idSeller
     * @return
     */
    fun getOrder(idSeller: String): Flow<Resource<GenericResponse<OrderModel>>> = flow {
        emit(Resource.loading(null))

        val res = safeCall.enqueue(idSeller, converter::converterMessageError, getService::getAllOrder)
        emit(res)
    }.flowOn(Dispatchers.IO)

    /**
     * Get detail order
     *
     * @param idOrder
     * @return
     */
    fun getDetailOrder(idOrder: String): Flow<Resource<GetDetailOrderResponse>> = flow {
        emit(Resource.loading(null))

        val res = safeCall.enqueueA(idOrder, converter::converterGenericError, getService::getDetailOrder)
        emit(res)
    }.flowOn(Dispatchers.IO)

    /**
     * Delete menu
     *
     * @param idFish
     * @return
     */
    fun deleteMenu(idFish: String): Flow<Resource<MessageResponse>> = flow {
        emit(Resource.loading(null))

        val res = safeCall.enqueue(idFish, converter::converterMessageError, postService::deleteMenu)
        emit(res)
    }.flowOn(Dispatchers.IO)

    /**
     * Edit menu
     *
     * @param idFish
     * @param request
     * @return
     */
    fun editMenu(idFish: String, request: AddRequest): Flow<Resource<MessageResponse>> = flow {
        emit(Resource.loading(null))

        val name = request.name.toRequestBody("text/plain".toMediaType())
        val weight = request.weight.toRequestBody("text/plain".toMediaType())
        val desc = request.description.toRequestBody("text/plain".toMediaType())
        val price = request.price.toRequestBody("text/plain".toMediaType())

        val res = safeCall.enqueueEdt(idFish, name, weight, desc, price, converter::converterAddError, postService::editMenu)
        emit(res)
    }.flowOn(Dispatchers.IO)

    /**
     * Upload image
     *
     * @param url
     * @param file
     * @return
     */
    fun uploadImage(url: String, file: File): Flow<Resource<MessageResponse>> = flow {
        emit(Resource.loading(null))
        val imageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, imageFile)
        safeCall.enqueueImage(url, imageMultipart, converter::converterMessageError, postService::uploadImage)

    }.flowOn(Dispatchers.IO)
}