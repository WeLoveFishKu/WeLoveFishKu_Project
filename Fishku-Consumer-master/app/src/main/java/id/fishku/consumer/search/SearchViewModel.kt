package id.fishku.consumer.search

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
class SearchViewModel @Inject constructor(
    authUseCase: AuthUseCase,
    private val fishUseCase: FishUseCase,
    private val cartUseCase: CartUseCase,
) : ViewModel() {

    private val _result = MutableLiveData<Resource<List<Fish>>>()
    val result: LiveData<Resource<List<Fish>>> get() = _result

    fun searchFish(query: String) {
        viewModelScope.launch {
            fishUseCase.searchFishes(query).collect { result ->
                _result.value = result
            }
        }
    }

    fun getAllFish() {
        viewModelScope.launch {
            fishUseCase.getAllFish().collect { result ->
                _result.value = result
            }
        }
    }

    private val getId = authUseCase.getId().asLiveData()

    private fun getCart(customerID: Int): LiveData<Resource<List<Cart>>> =
        cartUseCase.getCart(customerID).asLiveData()

    val cart: LiveData<Resource<List<Cart>>> = Transformations.switchMap(getId) { getCart(it) }
}