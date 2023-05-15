package id.fishku.consumer.core.domain.usecase

import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.repository.ChatRepository
import id.fishku.consumer.core.domain.params.CreateParams
import id.fishku.consumer.core.domain.params.NewChatParams
import id.fishku.consumer.core.domain.params.ReadParams
import id.fishku.consumer.core.domain.model.ChatArgs
import id.fishku.consumer.core.domain.model.SellerData
import id.fishku.consumer.core.domain.model.User
import id.fishku.consumer.core.domain.repository.Messages
import id.fishku.consumer.core.domain.repository.SellersData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatUsecaseImpl @Inject constructor(
    private val repository: ChatRepository
) : ChatUsecase {
    override suspend fun initUser(user: User) =
        repository.initUser(user)

    override fun createChat(createParams: CreateParams): Flow<Resource<ChatArgs>> =
        repository.createConnection(createParams)

    override suspend fun newMessage(
        newChatParams: NewChatParams
    ): Flow<Resource<Boolean>> =
        repository.newMessage(newChatParams)

    override fun messageList(chatId: String): Flow<Resource<Messages>> =
        repository.getMessage(chatId)

    override fun chatListSeller(userEmail: String): Flow<Resource<SellersData>> =
        repository.userChatList(userEmail)

    override suspend fun readMessage(readParams: ReadParams) {
        repository.readMessage(readParams)
    }

    override fun getListenerUser(userEmail: String): Flow<Resource<Boolean>> =
        repository.getListenerUser(userEmail)

    override fun getSellerChat(consumerEmail: String): Flow<Resource<SellerData>> =
        repository.getSellerChat(consumerEmail)


}