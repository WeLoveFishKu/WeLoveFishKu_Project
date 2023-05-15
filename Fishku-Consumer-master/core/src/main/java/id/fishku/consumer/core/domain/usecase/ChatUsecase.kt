package id.fishku.consumer.core.domain.usecase

import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.params.CreateParams
import id.fishku.consumer.core.domain.params.NewChatParams
import id.fishku.consumer.core.domain.params.ReadParams
import id.fishku.consumer.core.domain.model.ChatArgs
import id.fishku.consumer.core.domain.model.SellerData
import id.fishku.consumer.core.domain.model.User
import id.fishku.consumer.core.domain.repository.Messages
import id.fishku.consumer.core.domain.repository.SellersData
import kotlinx.coroutines.flow.Flow

interface ChatUsecase {
    suspend fun initUser(user: User) : Flow<Resource<Boolean>>
    fun createChat(createParams: CreateParams) : Flow<Resource<ChatArgs>>
    suspend fun newMessage(newChatParams: NewChatParams) : Flow<Resource<Boolean>>
    fun messageList(chatId: String): Flow<Resource<Messages>>
    fun chatListSeller(userEmail: String): Flow<Resource<SellersData>>
    suspend fun readMessage(readParams: ReadParams)
    fun getListenerUser(userEmail: String): Flow<Resource<Boolean>>
    fun getSellerChat(consumerEmail: String): Flow<Resource<SellerData>>
}