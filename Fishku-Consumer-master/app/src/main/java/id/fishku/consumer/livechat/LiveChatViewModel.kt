package id.fishku.consumer.livechat

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.params.CreateParams
import id.fishku.consumer.core.domain.params.ReadParams
import id.fishku.consumer.core.domain.model.ChatArgs
import id.fishku.consumer.core.domain.repository.SellersData
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import id.fishku.consumer.core.domain.usecase.ChatUsecase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveChatViewModel @Inject constructor(
    authUseCase: AuthUseCase,
    private val chatUsecase: ChatUsecase
) : ViewModel() {

    private var _isListenUser = MutableLiveData<Resource<Boolean>>()
    val isListenUser: LiveData<Resource<Boolean>> get() = _isListenUser

    private var _sellerData = MutableLiveData<Resource<SellersData>>()
    val sellerChats: LiveData<Resource<SellersData>> get() = _sellerData

    val getEmail = authUseCase.getEmail().asLiveData()


    fun getSellerChats(userEmail: String){
        viewModelScope.launch {
            chatUsecase.chatListSeller(userEmail).collect{
                _sellerData.value = it
            }
        }
    }

    fun createConnection(createParams: CreateParams): LiveData<Resource<ChatArgs>> =
            chatUsecase.createChat(createParams).asLiveData()

    fun readMessage(readParams: ReadParams){
        viewModelScope.launch {
            chatUsecase.readMessage(readParams)
        }
    }

    fun getListenerUser(userEmail: String){
        viewModelScope.launch {
            chatUsecase.getListenerUser(userEmail).collect{
                _isListenUser.value = it
            }
        }
    }
}