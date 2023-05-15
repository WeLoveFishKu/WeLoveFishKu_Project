package id.fishku.consumer.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {

    fun saveActivity() {
        viewModelScope.launch {
            authUseCase.saveActivity()
        }
    }

    fun getFirstLaunch() = authUseCase.getFirstLaunch().asLiveData()
}