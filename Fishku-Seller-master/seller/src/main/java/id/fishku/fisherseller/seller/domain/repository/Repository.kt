package id.fishku.fisherseller.seller.domain.repository

import id.fishku.fishersellercore.core.Resource
import id.fishku.fishersellercore.model.MenuModel
import id.fishku.fishersellercore.model.OrderModel
import id.fishku.fishersellercore.requests.AddRequest
import id.fishku.fishersellercore.requests.LoginRequest
import id.fishku.fishersellercore.requests.RegisterRequest
import id.fishku.fishersellercore.response.*
import kotlinx.coroutines.flow.Flow
import java.io.File


/**
 * Repository
 *
 * @constructor Create empty Repository
 */
interface Repository {

    /**
     * Register
     *
     * @param request
     * @return
     */
    fun register(request: RegisterRequest): Flow<Resource<RegisterResponse>>

    /**
     * Login
     *
     * @param request
     * @return
     */
    fun login(request: LoginRequest): Flow<Resource<LoginResponse>>

    /**
     * Post menu
     *
     * @param request
     * @return
     */
    fun postMenu(request: AddRequest): Flow<Resource<AddResponse>>

    /**
     * Get all fish
     *
     * @param idSeller
     * @return
     */
    fun getAllFish(idSeller: String): Flow<Resource<GenericResponse<MenuModel>>>

    /**
     * Get order
     *
     * @param idSeller
     * @return
     */
    fun getOrder(idSeller: String): Flow<Resource<GenericResponse<OrderModel>>>

    /**
     * Get search fish
     *
     * @param idSeller
     * @param name
     * @return
     */
    fun getSearchFish(idSeller: String, name: String): Flow<Resource<GenericResponse<MenuModel>>>

    /**
     * Get detail order
     *
     * @param idOrder
     * @return
     */
    fun getDetailOrder(idOrder: String): Flow<Resource<GetDetailOrderResponse>>

    /**
     * Delete menu
     *
     * @param idFish
     * @return
     */
    fun deleteMenu(idFish: String): Flow<Resource<MessageResponse>>

    /**
     * Edit menu
     *
     * @param idFish
     * @param request
     * @return
     */
    fun editMenu(idFish: String, request: AddRequest): Flow<Resource<MessageResponse>>

    /**
     * Upload image
     *
     * @param url
     * @param file
     * @return
     */
    fun uploadImage(url: String, file: File): Flow<Resource<MessageResponse>>
}