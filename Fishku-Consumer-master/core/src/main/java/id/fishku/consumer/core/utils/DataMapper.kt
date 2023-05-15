package id.fishku.consumer.core.utils

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import id.fishku.consumer.core.data.source.local.entity.FishEntity
import id.fishku.consumer.core.data.source.local.entity.UserEntity
import id.fishku.consumer.core.data.source.remote.response.*
import id.fishku.consumer.core.domain.model.*

object DataMapper {
    fun loginResponseToLogin(data: LoginResponse): Login = Login(
        data.message,
        data.token,
        data.users?.get(0)?.let {
            User(
                id = it.id,
                name = it.name,
                email = it.email,
                password = it.password,
                phoneNumber = it.phoneNumber,
                address = it.address
            )
        }
    )
    fun mapUser(user: User): User =
        User(
            id = user.id,
            name = user.name,
            email = user.email
        )
    fun registerResponseToRegister(data: RegisterResponse): Register = Register(data.message)

    fun userToUserEntity(data: User): UserEntity = UserEntity(
        data.id!!,
        data.name!!,
        data.email!!,
        data.phoneNumber!!,
        data.address!!,
    )

    fun fishEntityToFish(data: FishEntity): Fish = Fish(
        fishID = data.idFish,
        name = data.name,
        price = data.price,
        location = data.location,
        photoUrl = data.photoUrl
    )

    fun fishResponseToFishEntity(data: FishItem): FishEntity = FishEntity(
        idFish = data.fishID,
        name = data.name,
        price = data.price,
        location = data.location ?: "",
        photoUrl = data.photoUrl ?: ""
    )

    fun fishResponseToFish(data: FishItem): Fish = Fish(
        fishID = data.fishID,
        name = data.name,
        price = data.price,
        location = data.location ?: "",
        photoUrl = data.photoUrl ?: ""
    )

    fun detailFishToFish(data: DetailFishItem): Fish = Fish(
        fishID = data.fishID,
        name = data.name,
        location = data.location,
        sellerName = data.sellerName,
        email = data.email,
        weight = data.weight,
        price = data.price,
        photoUrl = data.photoUrl ?: ""
    )

    fun cartResponseToCart(data: CartItem): Cart = Cart(
        fishID = data.fishID,
        consumerID = data.consumerID,
        notes = data.notes,
        price = data.price,
        cartID = data.cartID,
        fishName = data.fishName ?: "",
        weight = data.weight,
        photoUrl = data.photoUrl ?: "",
        sellerEmail = data.sellerEmail ?: ""
    )

    fun orderResponseToOrder(data: OrderItem): Order = Order(
        orderingID = data.orderingID,
        date = data.date,
        priceTotal = data.totalPrice,
        status = data.status,
        invoiceUrl = data.invoiceUrl ?: ""
    )

    fun filterOrder(data: List<Order>?): List<Order> {
        val orders = arrayListOf<Order>()
        data?.groupBy { it.orderingID }?.entries?.map { (_, order) ->
            order
        }?.sortedByDescending {
            it[0].date
        }?.forEach {
            val order = Order(
                orderingID = it[0].orderingID,
                date = it[0].date,
                priceTotal = it.sumOf { data -> data.priceTotal },
                status = it[0].status,
                invoiceUrl = it[0].invoiceUrl ?: ""
            )
            orders.add(order)
        }
        return orders
    }

    fun orderDetailResponseToOrderDetail(data: OrderDetailItem): OrderDetail = OrderDetail(
        fishName = data.fishName,
        weight = data.weight,
        photoUrl = data.photoUrl,
        fishPrice = data.fishPrice,
        status = data.status
    )

    fun inputOrderResponseToInputOrder(data: InputOrderResponse): InputOrder = InputOrder(
        message = data.message,
        orderingID = data.orderingID
    )

    fun fishDetectionToFishType(data: FishDetectionItem): FishType = FishType(
        fishID = data.id,
        name = data.name,
        photoUrl = data.photo ?: ""
    )

    fun toOrderDataJsonObject(list: ArrayList<Cart>, orderingID: Int): JsonObject {
        val cartIdArray = JsonArray()
        list.forEach {
            cartIdArray.add(it.cartID)
        }

        val orderData = JsonObject().apply {
            add("data", cartIdArray)
            addProperty("id_ordering", orderingID.toString())
        }
        return orderData
    }

    fun invoiceResponseToInvoice(data: InvoiceResponse): Invoice =
        Invoice(data.message!!, data.data?.invoiceUrl)
}