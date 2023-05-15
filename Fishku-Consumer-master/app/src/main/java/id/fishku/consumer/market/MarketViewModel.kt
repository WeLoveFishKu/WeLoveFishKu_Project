package id.fishku.consumer.market

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Cart
import id.fishku.consumer.core.domain.model.Fish
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import id.fishku.consumer.core.domain.usecase.CartUseCase
import id.fishku.consumer.core.domain.usecase.FishUseCase
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    authUseCase: AuthUseCase,
    private val fishUseCase: FishUseCase,
    private val cartUseCase: CartUseCase
) : ViewModel() {

    private val getId = authUseCase.getId().asLiveData()

    fun getAllFish(): LiveData<Resource<List<Fish>>> = fishUseCase.getAllFish().asLiveData()

    private fun getCart(customerID: Int): LiveData<Resource<List<Cart>>> =
        cartUseCase.getCart(customerID).asLiveData()

    val cart: LiveData<Resource<List<Cart>>> = Transformations.switchMap(getId) { getCart(it) }

}