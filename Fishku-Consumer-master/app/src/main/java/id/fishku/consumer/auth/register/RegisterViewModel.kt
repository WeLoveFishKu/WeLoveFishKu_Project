package id.fishku.consumer.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Register
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {
    private val _result = MutableLiveData<Resource<Register>>()
    val result: LiveData<Resource<Register>> get() = _result

    fun registerUser(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
    ) {
        viewModelScope.launch {
            authUseCase.registerUser(name, email, password, phoneNumber, "")
                .collect { result ->
                    _result.value = result
                }
        }
    }
}