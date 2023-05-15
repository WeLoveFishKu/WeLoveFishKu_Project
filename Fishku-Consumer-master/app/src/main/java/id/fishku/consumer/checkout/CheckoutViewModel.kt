package id.fishku.consumer.checkout

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.remote.request.NotificationRequest
import id.fishku.consumer.core.domain.model.Cart
import id.fishku.consumer.core.domain.model.InputOrder
import id.fishku.consumer.core.domain.model.Invoice
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import id.fishku.consumer.core.domain.usecase.NotifyUseCase
import id.fishku.consumer.core.domain.usecase.OrderUseCase
import id.fishku.consumer.core.utils.DataMapper
import id.fishku.consumer.core.utils.OrderStatus
import id.fishku.consumer.core.utils.toDateFormat
import kotlinx.coroutines.launch
import java.sql.Timestamp
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    authUseCase: AuthUseCase,
    private val orderUseCase: OrderUseCase,
    private val notifyUseCase: NotifyUseCase
) : ViewModel() {
    private val _resultOrderingID = MutableLiveData<Resource<InputOrder>>()
    val resultOrderingID: LiveData<Resource<InputOrder>> get() = _resultOrderingID

    private val _resultInvoice = MutableLiveData<Resource<Invoice>>()
    val resultInvoice: LiveData<Resource<Invoice>> get() = _resultInvoice

    private val _resultInputDetailOrder = MutableLiveData<Resource<String>>()
    val resultInputDetailOrder: LiveData<Resource<String>> get() = _resultInputDetailOrder

    val getAddress = authUseCase.getAddress().asLiveData()
    val getId = authUseCase.getId().asLiveData()
    val getEmail = authUseCase.getEmail().asLiveData()

    val getName = authUseCase.getName().asLiveData()

    fun inputOrder(notes: String, courier: String, address: String, invoiceUrl: String, latitude: Double, longitude: Double) {
        val date = Timestamp(System.currentTimeMillis()).toDateFormat()
        val status = OrderStatus.SENDING.status
        viewModelScope.launch {
            orderUseCase.inputOrder(date, notes, status, courier, address, invoiceUrl, latitude, longitude).collect {
                _resultOrderingID.value = it
            }
        }
    }

    fun inputOrderDetail(consumerID: Int, carts: ArrayList<Cart>, orderingID: Int) {
        val orderData = DataMapper.toOrderDataJsonObject(carts, orderingID)
        viewModelScope.launch {
            orderUseCase.inputOrderDetail(consumerID, orderData).collect {
                _resultInputDetailOrder.value = it
            }
        }
    }

    fun createInvoice(consumerID: Int, payerEmail: String, amount: Int) {
        val externalID = "$consumerID-${System.currentTimeMillis()}"
        viewModelScope.launch {
            orderUseCase.createInvoice(externalID, payerEmail, "Pembelian di fishku", amount)
                .collect {
                    _resultInvoice.value = it
                }
        }
    }

    fun pushNotification(request: NotificationRequest){
        viewModelScope.launch {
            notifyUseCase.pushNotification(request).collect{}
        }
    }

    fun getListenerToken(sellerEmail: String)=
        notifyUseCase.getListenerToken(sellerEmail)

}

