package id.fishku.consumer.dashboard

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Cart
import id.fishku.consumer.core.domain.model.Fish
import id.fishku.consumer.core.domain.model.User
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import id.fishku.consumer.core.domain.usecase.CartUseCase
import id.fishku.consumer.core.domain.usecase.ChatUsecase
import id.fishku.consumer.core.domain.usecase.FishUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    authUseCase: AuthUseCase,
    private val fishUseCase: FishUseCase,
    private val cartUseCase: CartUseCase,

) : ViewModel() {


    private val getId = authUseCase.getId().asLiveData()

    fun getAllFish(): LiveData<Resource<List<Fish>>> = fishUseCase.getAllFish().asLiveData()

    private fun getCart(customerID: Int): LiveData<Resource<List<Cart>>> =
        cartUseCase.getCart(customerID).asLiveData()

    val cart: LiveData<Resource<List<Cart>>> = Transformations.switchMap(getId) { getCart(it) }


}