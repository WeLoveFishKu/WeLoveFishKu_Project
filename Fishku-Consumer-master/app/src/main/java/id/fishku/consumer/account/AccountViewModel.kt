package id.fishku.consumer.account

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.domain.model.User
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {


    fun getId() = authUseCase.getId().asLiveData()

    fun getName() = authUseCase.getName().asLiveData()

    fun getEmail() = authUseCase.getEmail().asLiveData()

    fun getPhoneNumber() = authUseCase.getPhoneNumber().asLiveData()

    fun getAddress() = authUseCase.getAddress().asLiveData()

    fun signOutWithGoogle(googleClient: GoogleSignInClient) {
        viewModelScope.launch {
            authUseCase.signOutWithGoogle(googleClient).collect {}
        }
    }

    fun linkedWithGoogle(userModel: User){
        viewModelScope.launch {
            authUseCase.initUser(userModel).collect{}
        }
    }

    fun signWithGoogle(data: Intent?) =
        authUseCase.signWithGoogle(data).asLiveData()

    fun deleteUserLinked(userId: String){
        viewModelScope.launch {
            authUseCase.deleteUserLinked(userId)
        }
    }

    fun userIsLinked(userEmail: String)=
        authUseCase.getUserIsLinked(userEmail).asLiveData()
}