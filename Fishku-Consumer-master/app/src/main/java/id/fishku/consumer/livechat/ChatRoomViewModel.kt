package id.fishku.consumer.livechat

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.remote.request.NotificationRequest
import id.fishku.consumer.core.domain.params.NewChatParams
import id.fishku.consumer.core.domain.params.ReadParams
import id.fishku.consumer.core.domain.model.SellerData
import id.fishku.consumer.core.domain.repository.Messages
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import id.fishku.consumer.core.domain.usecase.ChatUsecase
import id.fishku.consumer.core.domain.usecase.NotifyUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    authUseCase: AuthUseCase,
    private val chatUsecase: ChatUsecase,
    private val  notifyUseCase: NotifyUseCase
): ViewModel() {
    private var _newMessage = MutableLiveData<Resource<Boolean>>()
    val newMessage: LiveData<Resource<Boolean>> get() = _newMessage

    private var _messages = MutableLiveData<Resource<Messages>>()
    val messages: LiveData<Resource<Messages>> get() = _messages

    private var _seller = MutableLiveData<Resource<SellerData>>()
    val seller: LiveData<Resource<SellerData>> get() = _seller

    val getEmail = authUseCase.getEmail().asLiveData()

    fun writeMessage(newChatParams: NewChatParams){
        viewModelScope.launch {
            chatUsecase.newMessage(newChatParams).collect{
                _newMessage.value = it
            }
        }
    }

    fun getMessageList(chatId: String){
        viewModelScope.launch {
            chatUsecase.messageList(chatId).collect{
                _messages.value = it
            }
        }
    }

    fun getSellerChat(sellerEmail: String){
        viewModelScope.launch {
            chatUsecase.getSellerChat(sellerEmail).collect{
                _seller.value = it
            }
        }
    }
    fun readMessage(readParams: ReadParams){
        viewModelScope.launch {
            chatUsecase.readMessage(readParams)
        }
    }
    fun pushNotification(request: NotificationRequest){
        viewModelScope.launch {
            notifyUseCase.pushNotification(request).collect{}
        }
    }
}