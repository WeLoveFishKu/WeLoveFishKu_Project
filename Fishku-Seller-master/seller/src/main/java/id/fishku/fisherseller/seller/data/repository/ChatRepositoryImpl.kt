package id.fishku.fisherseller.seller.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.toObject
import id.fishku.fisherseller.seller.data.firebase.FirebaseDatasource
import id.fishku.fisherseller.seller.domain.params.CreateParams
import id.fishku.fisherseller.seller.domain.params.NewChatParams
import id.fishku.fisherseller.seller.domain.params.ReadParams
import id.fishku.fisherseller.seller.domain.repository.ChatRepository
import id.fishku.fisherseller.seller.domain.repository.ConsumersData
import id.fishku.fisherseller.seller.domain.repository.Messages
import id.fishku.fishersellercore.core.ResourceState
import id.fishku.fishersellercore.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val fireSource: FirebaseDatasource
) : ChatRepository {

    companion object {
        const val CONNECTIONS = "connections"
        const val IS_READ = "read"
        const val RECEIVER = "receiver"
        const val TIME_UPDATE = "timeUpdate"
        const val CONNECTING = "connecting"
        const val STATUS = "status"
        const val CONNECTED = "connected"
        const val LAST_MESSAGE = "lastMessage"
        const val LAST_TIME = "lastTime"
        const val TOTAL_UNREAD = "total_unread"
    }

    override suspend fun initUser(user: UserModel): Flow<ResourceState<Boolean>> = flow {
        emit(ResourceState.Loading())
        try {
            val userData = fireSource.userRef().whereEqualTo("email", user.email).get().await()

            if (userData.documents.size == 0) {
                val userEmail = user.email!!
                fireSource.userRef().document(userEmail).set(user.toMap()).await()
                emit(ResourceState.Success(true))
            }

        } catch (e: Exception) {
            emit(ResourceState.Error(e.message.toString(), null))
        }
    }

    override fun createConnection(createParams: CreateParams): Flow<ResourceState<ChatArgs>> =
        flow {
            emit(ResourceState.Loading())
            try {
                val currentEmail = createParams.userEmail
                val listConnection = listOf(
                    listOf(createParams.userEmail, createParams.consumerEmail),
                    listOf(createParams.consumerEmail, createParams.userEmail)
                )
                val checkConnectionChat =
                    fireSource.chatRef().whereIn(CONNECTIONS, listConnection).get().await()

                val chatUser = ChatUser(
                    connection = createParams.consumerEmail,
                    total_unread = 0,
                    lastTime = Timestamp.now()
                )
                val chatId = if (checkConnectionChat.documents.size == 0) {
                    val connections = listOf(createParams.userEmail, createParams.consumerEmail)
                    val chat = mapOf(
                        STATUS to CONNECTING,
                        CONNECTIONS to connections
                    )
                    val chatId = fireSource.chatRef().add(chat).await().id

                    fireSource.userChatsRef(currentEmail).document(chatId).set(chatUser.toMap())
                        .await()
                    chatId
                } else {
                    val chatId = checkConnectionChat.documents.first().id
                    fireSource.userChatsRef(currentEmail).document(chatId).set(chatUser.toMap())
                        .await()
                    chatId
                }
                val chatArgs =
                    ChatArgs(
                        consumerEmail = createParams.consumerEmail,
                        chatId = chatId
                    )

                emit(ResourceState.Success(chatArgs))
            } catch (e: Exception) {
                emit(ResourceState.Error(e.message.toString()))
            }
        }

    override suspend fun newMessage(
        newChatParams: NewChatParams
    ): Flow<ResourceState<Boolean>> = flow {
        emit(ResourceState.Loading())
        try {
            val date = Timestamp.now()
            val currentEmail = newChatParams.userEmail
            val toConsumerEmail = newChatParams.chatArgs.consumerEmail
            val timeUpdate = Timestamp.now()
            if (newChatParams.msg.isNotEmpty()) {

                val message = Message(
                    newChatParams.chatArgs.consumerEmail,
                    newChatParams.userEmail,
                    newChatParams.msg,
                    false,
                    timeUpdate
                )
                fireSource.chatMessageRef(newChatParams.chatArgs.chatId).add(message.toMap())
                    .await()

                val chat = mapOf(
                    STATUS to CONNECTED
                )
                fireSource.chatRef().document(newChatParams.chatArgs.chatId)
                    .update(chat).await()
            }
            val lastTimeMessage = mapOf(LAST_TIME to date, LAST_MESSAGE to newChatParams.msg)
            fireSource.userRef().document(currentEmail).update(lastTimeMessage)
            fireSource.userRef().document(toConsumerEmail).update(lastTimeMessage)

            val updateTotalRead = fireSource.chatMessageRef(newChatParams.chatArgs.chatId)
                .whereEqualTo(IS_READ, false)
                .whereEqualTo(RECEIVER, toConsumerEmail)
                .get().await()

            val totalUnread = updateTotalRead.documents.size
            val lastTotal = mapOf(
                TOTAL_UNREAD to totalUnread
            )
            fireSource.userChatsRef(toConsumerEmail).document(newChatParams.chatArgs.chatId)
                .update(lastTotal)

            emit(ResourceState.Success(true))
        } catch (e: Exception) {
            emit(ResourceState.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    override fun userChatList(userEmail: String): Flow<ResourceState<ConsumersData>> = flow {
        emit(ResourceState.Loading())
        try {
            val checkMessage =
                fireSource.chatRef().whereArrayContainsAny(CONNECTIONS, listOf(userEmail))
                    .whereEqualTo(STATUS, CONNECTED).get().await()
            val listChat = mutableListOf<ConsumerReadData>()
            if (checkMessage.documents.size != 0) {
                listChat.clear()
                for (chat in checkMessage.documents) {
                    val consumerEmail = (chat.get(CONNECTIONS) as List<*>).first().toString()
                    val consumer = fireSource.userRef().document(consumerEmail).get().await()
                    val unread = fireSource.userChatsRef(userEmail).document(chat.id).get().await()
                    if (unread.data != null) {
                        val chatUser = unread.toObject(ChatUser::class.java)

                        val consumerData = consumer.toObject<ConsumerData>()
                        if (chatUser != null && consumerData != null) {
                            val consumerReadData = ConsumerReadData(chatUser, consumerData)
                            listChat.add(consumerReadData)
                        }
                    } else {
                        val consumerData = consumer.toObject<ConsumerData>()
                        if (consumerData != null) {
                            val consumerReadData = ConsumerReadData(ChatUser(), consumerData)
                            listChat.add(consumerReadData)
                        }
                    }

                }
            }
            emit(ResourceState.Success(listChat))
        } catch (e: Exception) {
            emit(ResourceState.Error(e.message.toString()))
        }

    }

    override fun getMessage(chatId: String) = callbackFlow<ResourceState<Messages>> {

        val snapshotListener =
            fireSource.chatMessageRef(chatId).orderBy(TIME_UPDATE)
                .addSnapshotListener { snapshot, error ->
                    val response = if (snapshot != null) {
                        val chats = snapshot.toObjects(Message::class.java)
                        ResourceState.Success(chats)
                    } else {
                        ResourceState.Error(error?.message.toString())
                    }
                    trySend(response)
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun readMessage(readParams: ReadParams) {
        val updateTotalRead = fireSource.chatMessageRef(readParams.chatId)
            .whereEqualTo(IS_READ, false)
            .whereEqualTo(RECEIVER, readParams.userEmail)
            .get().await()

        updateTotalRead.documents.forEach { doc ->
            fireSource.chatMessageRef(readParams.chatId).document(doc.id).update(
                mapOf(IS_READ to true)
            ).await()
        }

        val lastTotal = mapOf(
            TOTAL_UNREAD to 0
        )

        fireSource.userChatsRef(readParams.userEmail).document(readParams.chatId)
            .update(lastTotal).await()
    }

    override fun getListenerUser(userEmail: String) = callbackFlow {
        val snapshotListener =
            fireSource.userRef().document(userEmail).addSnapshotListener { snapshot, _ ->
                val response = if (snapshot != null) {

                    ResourceState.Success(true)
                } else {
                    ResourceState.Error("")
                }
                trySend(response)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override fun getConsumerChat(consumerEmail: String) = callbackFlow {

        val snapshotListener =
            fireSource.userRef().document(consumerEmail).addSnapshotListener { snapshot, err ->
                val response = if (snapshot != null) {
                    val consumerData = snapshot.toObject(ConsumerData::class.java)

                    ResourceState.Success(consumerData ?: ConsumerData())
                } else {
                    ResourceState.Error(err?.message.toString())
                }
                trySend(response)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }

    // Login auth


}