package id.fishku.consumer.core.domain.repository

import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.params.CreateParams
import id.fishku.consumer.core.domain.params.NewChatParams
import id.fishku.consumer.core.domain.params.ReadParams
import id.fishku.consumer.core.domain.model.*
import kotlinx.coroutines.flow.Flow

typealias SellersData = List<SellerReadData>
typealias Messages = List<Message>

interface IChatRepository {
    suspend fun initUser(user: User): Flow<Resource<Boolean>>

    fun createConnection(createParams: CreateParams) : Flow<Resource<ChatArgs>>

    suspend fun newMessage(newChatParams: NewChatParams): Flow<Resource<Boolean>>

    fun userChatList(userEmail: String): Flow<Resource<SellersData>>

    fun getMessage(chatId: String) : Flow<Resource<Messages>>

    suspend fun readMessage(readParams: ReadParams)

    fun getListenerUser(userEmail: String): Flow<Resource<Boolean>>
    fun getSellerChat(sellerEmail: String): Flow<Resource<SellerData>>
}