package id.fishku.consumer.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Order
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import id.fishku.consumer.core.domain.usecase.OrderUseCase
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    authUseCase: AuthUseCase,
    private val orderUseCase: OrderUseCase,
) : ViewModel() {

    private val getId = authUseCase.getId().asLiveData()
    private fun getAllOrder(consumerID: Int) =
        orderUseCase.getAllOrderByIdConsumer(consumerID).asLiveData()

    val orders: LiveData<Resource<List<Order>>> =
        Transformations.switchMap(getId) { getAllOrder(it) }
}