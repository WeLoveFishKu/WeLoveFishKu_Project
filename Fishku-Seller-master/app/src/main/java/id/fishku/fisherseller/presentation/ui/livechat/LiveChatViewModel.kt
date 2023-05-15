package id.fishku.fisherseller.presentation.ui.livechat

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.fisherseller.seller.domain.params.ReadParams
import id.fishku.fisherseller.seller.domain.repository.ChatRepository
import id.fishku.fisherseller.seller.domain.repository.ConsumersData
import id.fishku.fishersellercore.core.ResourceState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private var _isListenUser = MutableLiveData<ResourceState<Boolean>>()
    val isListenUser: LiveData<ResourceState<Boolean>> get() = _isListenUser

    fun getSellerChats(userEmail: String): LiveData<ResourceState<ConsumersData>> =
        repository.userChatList(userEmail).asLiveData()



    fun readMessage(readParams: ReadParams){
        viewModelScope.launch {
            repository.readMessage(readParams)
        }
    }

    fun getListenerUser(userEmail: String){
        viewModelScope.launch {
            repository.getListenerUser(userEmail).collect{
                _isListenUser.value = it
            }
        }
    }
}