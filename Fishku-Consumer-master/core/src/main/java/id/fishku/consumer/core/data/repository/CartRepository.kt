package id.fishku.consumer.core.data.repository

import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.remote.RemoteDataSource
import id.fishku.consumer.core.data.source.remote.network.ApiResponse
import id.fishku.consumer.core.domain.model.Cart
import id.fishku.consumer.core.domain.repository.ICartRepository
import id.fishku.consumer.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    ICartRepository {

    override fun getCart(consumerID: Int): Flow<Resource<List<Cart>>> = flow {
        emit(Resource.Loading())
        when (val response = remoteDataSource.getCart(consumerID).first()) {
            is ApiResponse.Success -> {
                val carts = response.data.map {
                    DataMapper.cartResponseToCart(it)
                }
                emit(Resource.Success(carts))
            }

            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))

            is ApiResponse.Empty -> {}
        }
    }

    override fun editCart(cartID: Int, newWeight: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when (val response = remoteDataSource.editCart(cartID, newWeight).first()) {
            is ApiResponse.Success -> {
                val message = response.data
                emit(Resource.Success(message))
            }

            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))

            is ApiResponse.Empty -> {}
        }
    }

    override fun deleteCart(cartID: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when (val response = remoteDataSource.deleteCart(cartID).first()) {
            is ApiResponse.Success -> {
                val message = response.data
                emit(Resource.Success(message))
            }

            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))

            is ApiResponse.Empty -> {}
        }
    }

    override fun inputCart(
        fishID: Int,
        consumerID: Int,
        notes: String,
        weight: Int,
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when (val response =
            remoteDataSource.inputCart(fishID, consumerID, notes, weight).first()) {
            is ApiResponse.Success -> {
                val message = response.data
                emit(Resource.Success(message))
            }

            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))

            is ApiResponse.Empty -> {}
        }
    }
}