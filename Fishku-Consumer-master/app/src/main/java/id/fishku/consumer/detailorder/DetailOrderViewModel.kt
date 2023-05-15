package id.fishku.consumer.detailorder

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.OrderDetail
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import id.fishku.consumer.core.domain.usecase.OrderUseCase
import javax.inject.Inject

@HiltViewModel
class DetailOrderViewModel @Inject constructor(
    authUseCase: AuthUseCase,
    private val orderUseCase: OrderUseCase,
) : ViewModel() {

    private val getId = authUseCase.getId().asLiveData()
    private fun getDetailOrder(consumerID: Int, orderingID: Int) =
        orderUseCase.getOrderDetail(consumerID, orderingID).asLiveData()

    fun orderDetail(orderingID: Int): LiveData<Resource<List<OrderDetail>>> =
        Transformations.switchMap(getId) { getDetailOrder(it, orderingID) }
}