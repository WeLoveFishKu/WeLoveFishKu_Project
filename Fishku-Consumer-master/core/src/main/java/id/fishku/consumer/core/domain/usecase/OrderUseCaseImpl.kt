package id.fishku.consumer.core.domain.usecase

import com.google.gson.JsonObject
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.InputOrder
import id.fishku.consumer.core.domain.model.Invoice
import id.fishku.consumer.core.domain.model.Order
import id.fishku.consumer.core.domain.model.OrderDetail
import id.fishku.consumer.core.domain.repository.IOrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OrderUseCaseImpl @Inject constructor(private val orderRepository: IOrderRepository) :
    OrderUseCase {
    override fun getAllOrderByIdConsumer(consumerID: Int): Flow<Resource<List<Order>>> =
        orderRepository.getAllOrderByIdConsumer(consumerID)

    override fun getOrderDetail(
        consumerID: Int,
        orderingID: Int,
    ): Flow<Resource<List<OrderDetail>>> = orderRepository.getOrderDetail(consumerID, orderingID)

    override fun inputOrder(
        date: String,
        notes: String,
        status: String,
        courier: String,
        address: String,
        invoiceUrl: String,
        latitude: Double,
        longitude: Double,
    ): Flow<Resource<InputOrder>> =
        orderRepository.inputOrder(date, notes, status, courier, address, invoiceUrl, latitude, longitude)

    override fun inputOrderDetail(consumerID: Int, orderData: JsonObject): Flow<Resource<String>> =
        orderRepository.inputOrderDetail(consumerID, orderData)

    override fun createInvoice(
        externalID: String,
        payerEmail: String,
        description: String,
        amount: Int,
    ): Flow<Resource<Invoice>> =
        orderRepository.createInvoice(externalID, payerEmail, description, amount)
}