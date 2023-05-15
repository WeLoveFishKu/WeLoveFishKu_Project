package id.fishku.consumer.detailfish

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Cart
import id.fishku.consumer.core.domain.model.Fish
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import id.fishku.consumer.core.domain.usecase.CartUseCase
import id.fishku.consumer.core.domain.usecase.FishUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailFishViewModel @Inject constructor(
    private val fishUseCase: FishUseCase,
    private val cartUseCase: CartUseCase,
    authUseCase: AuthUseCase,
) : ViewModel() {

    val getId = authUseCase.getId().asLiveData()

    private val _inputResult = MutableLiveData<Resource<String>>()
    val inputResult: LiveData<Resource<String>> get() = _inputResult
    private val _currentCart = MutableLiveData<Resource<List<Cart>>>()
    val currentCart: LiveData<Resource<List<Cart>>> get() = _currentCart

    fun getDetailFish(fishID: Int): LiveData<Resource<Fish>> =
        fishUseCase.getDetailFish(fishID).asLiveData()

    fun inputCart(
        fishID: Int,
        consumerID: Int,
        notes: String,
        weight: Int,
    ) {
        viewModelScope.launch {
            cartUseCase.inputCart(fishID, consumerID, notes, weight).collect {
                _inputResult.value = it
            }
        }
    }

    private fun getCart(customerID: Int): LiveData<Resource<List<Cart>>> =
        cartUseCase.getCart(customerID).asLiveData()

    val cart: LiveData<Resource<List<Cart>>> = Transformations.switchMap(getId) { getCart(it) }

    fun getCurrentCart(consumerID: Int) {
        viewModelScope.launch {
            cartUseCase.getCart(consumerID).collect {
                _currentCart.value = it
            }
        }
    }

    fun editCart(cartID: Int, newWeight: Int) {
        viewModelScope.launch {
            cartUseCase.editCart(cartID, newWeight).collect {
                _inputResult.value = it
            }
        }
    }
}