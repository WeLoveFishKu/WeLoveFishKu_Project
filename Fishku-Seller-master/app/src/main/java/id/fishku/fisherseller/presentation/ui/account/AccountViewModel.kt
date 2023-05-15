package id.fishku.fisherseller.presentation.ui.account

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.fisherseller.seller.domain.repository.AuthRepository
import id.fishku.fishersellercore.model.UserModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository
)  : ViewModel() {


    fun signOutWithGoogle(googleClient: GoogleSignInClient) {
        viewModelScope.launch {
            authRepository.signOutWithGoogle(googleClient).collect {}
        }
    }
    fun linkedWithGoogle(userModel: UserModel){
        viewModelScope.launch {
            authRepository.initUser(userModel).collect{}
        }
    }

    fun signWithGoogle(data: Intent?) =
        authRepository.signWithGoogle(data).asLiveData()

    fun deleteUserLinked(userId: String){
        viewModelScope.launch {
            authRepository.deleteUserLinked(userId)
        }
    }

    fun userIsLinked(userEmail: String)=
        authRepository.getUserIsLinked(userEmail).asLiveData()
}