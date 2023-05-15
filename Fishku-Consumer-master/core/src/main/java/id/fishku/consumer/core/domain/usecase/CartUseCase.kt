package id.fishku.consumer.core.domain.usecase

import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Cart
import kotlinx.coroutines.flow.Flow

interface CartUseCase {
    fun getCart(consumerID: Int): Flow<Resource<List<Cart>>>
    fun editCart(cartID: Int, newWeight: Int): Flow<Resource<String>>
    fun deleteCart(cartID: Int): Flow<Resource<String>>
    fun inputCart(
        fishID: Int,
        consumerID: Int,
        notes: String,
        weight: Int,
    ): Flow<Resource<String>>
}