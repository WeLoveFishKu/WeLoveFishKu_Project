package id.fishku.fisherseller.presentation.ui.livechat

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.fisherseller.notification.domain.NotifyRepository
import id.fishku.fisherseller.seller.domain.params.CreateParams
import id.fishku.fisherseller.seller.domain.params.NewChatParams
import id.fishku.fisherseller.seller.domain.params.ReadParams
import id.fishku.fisherseller.seller.domain.repository.ChatRepository
import id.fishku.fisherseller.seller.domain.repository.Messages
import id.fishku.fishersellercore.core.ResourceState
import id.fishku.fishersellercore.model.ChatArgs
import id.fishku.fishersellercore.model.ConsumerData
import id.fishku.fishersellercore.requests.NotificationRequest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val repoNotify: NotifyRepository
) : ViewModel() {
    private var _newMessage = MutableLiveData<ResourceState<Boolean>>()
    val newMessage: LiveData<ResourceState<Boolean>> get() = _newMessage

    private var _messages = MutableLiveData<ResourceState<Messages>>()
    val messages: LiveData<ResourceState<Messages>> get() = _messages

    private var _consumer = MutableLiveData<ResourceState<ConsumerData>>()
    val consumer: LiveData<ResourceState<ConsumerData>> get() = _consumer

    fun writeMessage(newChatParams: NewChatParams){
        viewModelScope.launch {
            repository.newMessage(newChatParams).collect{
                _newMessage.value = it
            }
        }
    }

    fun getMessageList(chatId: String){
        viewModelScope.launch {
            repository.getMessage(chatId).collect{
                _messages.value = it
            }
        }
    }

    fun getConsumerChat(consumerEmail: String){
        viewModelScope.launch {
            repository.getConsumerChat(consumerEmail).collect{
                _consumer.value = it
            }
        }
    }

    fun readMessage(readParams: ReadParams){
        viewModelScope.launch {
            repository.readMessage(readParams)
        }
    }

    fun createConnection(createParams: CreateParams): LiveData<ResourceState<ChatArgs>> =
        repository.createConnection(createParams).asLiveData()

    fun pushNotification(request: NotificationRequest){
        viewModelScope.launch {
            repoNotify.pushNotification(request).collect{}
        }
    }
}