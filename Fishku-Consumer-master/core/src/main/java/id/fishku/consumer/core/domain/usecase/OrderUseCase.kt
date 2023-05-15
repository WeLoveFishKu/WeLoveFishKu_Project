package id.fishku.consumer.core.domain.usecase

import com.google.gson.JsonObject
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.InputOrder
import id.fishku.consumer.core.domain.model.Invoice
import id.fishku.consumer.core.domain.model.Order
import id.fishku.consumer.core.domain.model.OrderDetail
import kotlinx.coroutines.flow.Flow

interface OrderUseCase {
    fun getAllOrderByIdConsumer(consumerID: Int): Flow<Resource<List<Order>>>
    fun getOrderDetail(consumerID: Int, orderingID: Int): Flow<Resource<List<OrderDetail>>>
    fun inputOrder(
        date: String,
        notes: String,
        status: String,
        courier: String,
        address: String,
        invoiceUrl: String,
        latitude: Double,
        longitude: Double,
    ): Flow<Resource<InputOrder>>

    fun inputOrderDetail(consumerID: Int, orderData: JsonObject): Flow<Resource<String>>

    fun createInvoice(
        externalID: String,
        payerEmail: String,
        description: String,
        amount: Int,
    ): Flow<Resource<Invoice>>
}