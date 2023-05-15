package id.fishku.consumer.cart

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Cart
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import id.fishku.consumer.core.domain.usecase.CartUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    authUseCase: AuthUseCase,
    private val cartUseCase: CartUseCase,
) : ViewModel() {
    private val getId = authUseCase.getId().asLiveData()

    private val _editResult = MutableLiveData<Resource<String>>()
    val editResult: LiveData<Resource<String>> get() = _editResult

    private val _deleteResult = MutableLiveData<Resource<String>>()
    val deleteResult: LiveData<Resource<String>> get() = _deleteResult

    private val _deletedPosition = MutableLiveData<Int>()
    val deletedPosition: LiveData<Int> get() = _deletedPosition

    private fun getCart(customerID: Int): LiveData<Resource<List<Cart>>> =
        cartUseCase.getCart(customerID).asLiveData()

    val cart: LiveData<Resource<List<Cart>>> = Transformations.switchMap(getId) { getCart(it) }

    fun editCart(cartID: Int, newWeight: Int) {
        viewModelScope.launch {
            cartUseCase.editCart(cartID, newWeight).collect {
                _editResult.value = it
            }
        }
    }

    fun deleteCart(cartID: Int, position: Int) {
        viewModelScope.launch {
            cartUseCase.deleteCart(cartID).collect {
                if (it is Resource.Success) {
                    _deletedPosition.value = position
                }
                _deleteResult.value = it
            }
        }
    }
}