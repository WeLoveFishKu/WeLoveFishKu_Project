package id.fishku.fisherseller.presentation.ui.auth

import android.content.Intent
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.fisherseller.notification.domain.NotifyRepository
import id.fishku.fisherseller.notification.params.ParamsToken
import id.fishku.fisherseller.seller.domain.repository.AuthRepository
import id.fishku.fisherseller.seller.domain.repository.ChatRepository
import id.fishku.fisherseller.seller.domain.repository.Repository
import id.fishku.fishersellercore.core.Resource
import id.fishku.fishersellercore.core.ResourceState
import id.fishku.fishersellercore.model.UserModel
import id.fishku.fishersellercore.requests.LoginRequest
import id.fishku.fishersellercore.requests.RegisterRequest
import id.fishku.fishersellercore.response.LoginResponse
import id.fishku.fishersellercore.response.RegisterResponse
import id.fishku.fishersellercore.util.helper.User
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Auth view model
 *
 * @property repo
 * @constructor Create empty Auth view model
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: Repository,
    private val chatRepo: ChatRepository,
    private val repoNotify: NotifyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _user = MutableLiveData<ResourceState<User>>()
    val user: LiveData<ResourceState<User>> get() = _user

    fun register(request: RegisterRequest): LiveData<Resource<RegisterResponse>> =
        repo.register(request).asLiveData()

    fun login(request: LoginRequest): LiveData<Resource<LoginResponse>> =
        repo.login(request).asLiveData()

    fun initUser(userModel: UserModel, token: String?) {
        viewModelScope.launch {
            chatRepo.initUser(userModel).collect {
                updateToken(ParamsToken(token!!, userModel.email!!))
            }
        }
    }

    private fun updateToken(paramsToken: ParamsToken) {
        viewModelScope.launch {
            repoNotify.updateToken(paramsToken).collect {}
        }
    }

    fun signWithGoogle(data: Intent?) {
        viewModelScope.launch {
            authRepository.signWithGoogle(data).collect {
                _user.value = it
            }
        }
    }

    fun logoutGoogle(googleSignInClient: GoogleSignInClient){
        viewModelScope.launch {
            authRepository.signOutWithGoogle(googleSignInClient).collect{}
        }
    }

    fun userLinked(emailLink: String)=
        authRepository.getUserLinked(emailLink).asLiveData()
}