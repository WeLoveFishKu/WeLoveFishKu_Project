package id.fishku.fisherseller.seller.domain.repository

import id.fishku.fisherseller.seller.domain.params.CreateParams
import id.fishku.fisherseller.seller.domain.params.NewChatParams
import id.fishku.fisherseller.seller.domain.params.ReadParams
import id.fishku.fishersellercore.core.ResourceState
import id.fishku.fishersellercore.model.*
import kotlinx.coroutines.flow.Flow

typealias ConsumersData = List<ConsumerReadData>
typealias Messages = List<Message>

interface ChatRepository {
    suspend fun initUser(user: UserModel): Flow<ResourceState<Boolean>>
    fun createConnection(createParams: CreateParams) : Flow<ResourceState<ChatArgs>>

    suspend fun newMessage(newChatParams: NewChatParams): Flow<ResourceState<Boolean>>

    fun userChatList(userEmail: String): Flow<ResourceState<ConsumersData>>

    fun getMessage(chatId: String) : Flow<ResourceState<Messages>>
    suspend fun readMessage(readParams: ReadParams)
    fun getListenerUser(userEmail: String): Flow<ResourceState<Boolean>>
    fun getConsumerChat(consumerEmail: String): Flow<ResourceState<ConsumerData>>
}