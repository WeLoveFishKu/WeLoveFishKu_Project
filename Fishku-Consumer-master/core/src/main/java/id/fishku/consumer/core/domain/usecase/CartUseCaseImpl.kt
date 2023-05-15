package id.fishku.consumer.core.domain.usecase

import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Cart
import id.fishku.consumer.core.domain.repository.ICartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CartUseCaseImpl @Inject constructor(private val cartRepository: ICartRepository) :
    CartUseCase {
    override fun getCart(consumerID: Int): Flow<Resource<List<Cart>>> =
        cartRepository.getCart(consumerID)

    override fun editCart(cartID: Int, newWeight: Int): Flow<Resource<String>> =
        cartRepository.editCart(cartID, newWeight)

    override fun deleteCart(cartID: Int): Flow<Resource<String>> =
        cartRepository.deleteCart(cartID)

    override fun inputCart(
        fishID: Int,
        consumerID: Int,
        notes: String,
        weight: Int,
    ): Flow<Resource<String>> = cartRepository.inputCart(fishID, consumerID, notes, weight)
}