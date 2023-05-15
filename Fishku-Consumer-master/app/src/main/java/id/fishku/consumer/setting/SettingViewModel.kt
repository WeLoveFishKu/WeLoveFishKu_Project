package id.fishku.consumer.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {
    fun deleteSession() {
        viewModelScope.launch {
            authUseCase.deleteSession()
        }
    }

    fun signOutWithGoogle(googleClient: GoogleSignInClient) {
        viewModelScope.launch {
            authUseCase.signOutWithGoogle(googleClient).collect {}
        }
    }
}