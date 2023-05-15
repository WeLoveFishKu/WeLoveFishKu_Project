package id.fishku.consumer.auth.login

import android.content.Intent
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Login
import id.fishku.consumer.core.domain.model.User
import id.fishku.consumer.core.domain.params.ParamsToken
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import id.fishku.consumer.core.domain.usecase.ChatUsecase
import id.fishku.consumer.core.domain.usecase.NotifyUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val chatUsecase: ChatUsecase,
    private val notifyUseCase: NotifyUseCase
    ) : ViewModel() {

    private val _user = MutableLiveData<Resource<User>>()
    val user: LiveData<Resource<User>> get() = _user

    private val _result = MutableLiveData<Resource<Login>>()
    val result: LiveData<Resource<Login>> get() = _result

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            authUseCase.loginUser(email, password).collect { result ->
                _result.value = result
            }
        }
    }

    fun saveSession(token: String, user: User){
        viewModelScope.launch {
            authUseCase.saveSession(token, user)
        }
    }

    fun getToken() = authUseCase.getToken().asLiveData()

    fun initUser(user: User, token: String?){
        viewModelScope.launch {
            chatUsecase.initUser(user).collect{
                updateToken(ParamsToken(token!!, user.email!!))
            }
        }
    }
    fun updateToken(paramsToken: ParamsToken){
        viewModelScope.launch {
            notifyUseCase.updateToken(paramsToken).collect{}
        }
    }

    fun signWithGoogle(data: Intent?) {
        viewModelScope.launch {
            authUseCase.signWithGoogle(data).collect {
                _user.value = it
            }
        }
    }
    fun signOutGoogle(googleSignInClient: GoogleSignInClient){
        viewModelScope.launch {
            authUseCase.signOutWithGoogle(googleSignInClient).collect{}
        }
    }

    fun userLinked(emailLink: String)=
        authUseCase.getUserLinked(emailLink).asLiveData()
}