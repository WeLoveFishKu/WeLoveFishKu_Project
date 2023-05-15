package id.fishku.consumer.core.data.repository

import com.google.gson.JsonObject
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.remote.RemoteDataSource
import id.fishku.consumer.core.data.source.remote.network.ApiResponse
import id.fishku.consumer.core.domain.model.InputOrder
import id.fishku.consumer.core.domain.model.Invoice
import id.fishku.consumer.core.domain.model.Order
import id.fishku.consumer.core.domain.model.OrderDetail
import id.fishku.consumer.core.domain.repository.IOrderRepository
import id.fishku.consumer.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OrderRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) :
    IOrderRepository {
    override fun getAllOrderByIdConsumer(consumerID: Int): Flow<Resource<List<Order>>> = flow {
        emit(Resource.Loading())
        when (val response = remoteDataSource.getAllOrderByIdConsumer(consumerID).first()) {
            is ApiResponse.Success -> {
                val orders = response.data.map {
                    DataMapper.orderResponseToOrder(it)
                }
                emit(Resource.Success(orders))
            }

            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))

            is ApiResponse.Empty -> {}
        }
    }

    override fun getOrderDetail(
        consumerID: Int,
        orderingID: Int,
    ): Flow<Resource<List<OrderDetail>>> = flow {
        emit(Resource.Loading())
        when (val response = remoteDataSource.getOrderDetail(consumerID, orderingID).first()) {
            is ApiResponse.Success -> {
                val orders = response.data.map {
                    DataMapper.orderDetailResponseToOrderDetail(it)
                }
                emit(Resource.Success(orders))
            }

            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))

            is ApiResponse.Empty -> {}
        }
    }

    override fun inputOrder(
        date: String,
        notes: String,
        status: String,
        courier: String,
        address: String,
        invoiceUrl: String,
        latitude: Double,
        longitude: Double,
    ): Flow<Resource<InputOrder>> = flow {
        emit(Resource.Loading())
        when (val response =
            remoteDataSource.inputOrder(date, notes, status, courier, address, invoiceUrl, latitude, longitude)
                .first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.inputOrderResponseToInputOrder(response.data)
                emit(Resource.Success(data))
            }

            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))

            is ApiResponse.Empty -> {}
        }
    }

    override fun inputOrderDetail(
        consumerID: Int,
        orderData: JsonObject,
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when (val response =
            remoteDataSource.inputOrderDetail(consumerID, orderData).first()) {

            is ApiResponse.Success -> {
                val message = response.data.message
                if (message != null) {
                    emit(Resource.Success(message))
                }
            }

            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))

            is ApiResponse.Empty -> {}
        }
    }

    override fun createInvoice(
        externalID: String,
        payerEmail: String,
        description: String,
        amount: Int,
    ): Flow<Resource<Invoice>> = flow {
        emit(Resource.Loading())
        when (val response =
            remoteDataSource.createInvoice(externalID, payerEmail, description, amount).first()) {

            is ApiResponse.Success -> {
                val data = DataMapper.invoiceResponseToInvoice(response.data)
                emit(Resource.Success(data))
            }

            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))

            is ApiResponse.Empty -> {}
        }
    }
}