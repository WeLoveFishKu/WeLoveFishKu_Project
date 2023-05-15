package id.fishku.consumer.core.data.repository

import com.google.firebase.Timestamp
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.firebase.FirebaseDatasource
import id.fishku.consumer.core.domain.model.*
import id.fishku.consumer.core.domain.params.CreateParams
import id.fishku.consumer.core.domain.params.NewChatParams
import id.fishku.consumer.core.domain.params.ReadParams
import id.fishku.consumer.core.domain.repository.IChatRepository
import id.fishku.consumer.core.domain.repository.Messages
import id.fishku.consumer.core.domain.repository.SellersData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Chat repository
 *
 * @property fireSource
 * @constructor Create empty Chat repository
 */
class ChatRepository @Inject constructor(
    private val fireSource: FirebaseDatasource
) : IChatRepository {

    companion object {
        const val CONNECTIONS = "connections"
        const val ISREAD = "read"
        const val RECEIVER = "receiver"
        const val TIME_UPDATE = "timeUpdate"
        const val CONNECTING = "connecting"
        const val STATUS = "status"
        const val CONNECTED = "connected"
        const val LAST_MESSAGE = "lastMessage"
        const val LAST_TIME = "lastTime"
        const val TOTAL_UNREAD = "total_unread"
        const val SECOND_CONNECTION = 1
    }

    /**
     * Init user
     *
     * @param user
     * @return
     */
    override suspend fun initUser(user: User): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val userData = fireSource.userRef().whereEqualTo("email", user.email).get().await()
            println(userData.documents)
            if (userData.documents.size == 0) {
                val userEmail = user.email!!
                fireSource.userRef().document(userEmail).set(user.toMap()).await()
                emit(Resource.Success(true))
            }

            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)


    override fun createConnection(createParams: CreateParams): Flow<Resource<ChatArgs>> =
        flow {
            emit(Resource.Loading())
            try {
                val currentEmail = createParams.userEmail
                val listConnection = listOf(
                    listOf(createParams.userEmail, createParams.sellerEmail),
                    listOf(createParams.sellerEmail, createParams.userEmail)
                )
                val checkConnectionChat =
                    fireSource.chatRef().whereIn(CONNECTIONS, listConnection).get().await()

                val chatUser = ChatUser(
                    connection = createParams.sellerEmail,
                    total_unread = 0,
                    lastTime = Timestamp.now()
                )
                val chatId = if (checkConnectionChat.documents.size == 0) {
                    val connections = listOf(createParams.userEmail, createParams.sellerEmail)
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

                    chatId
                }
                val chatArgs =
                    ChatArgs(
                        sellerEmail = createParams.sellerEmail,
                        chatId = chatId
                    )

                emit(Resource.Success(chatArgs))
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun newMessage(
        newChatParams: NewChatParams
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val date = Timestamp.now()
            val currentEmail = newChatParams.userEmail
            val toSellerEmail = newChatParams.chatArgs.sellerEmail
            val timeUpdate = Timestamp.now()
            if (newChatParams.msg.isNotEmpty()) {

                val message = Message(
                    newChatParams.chatArgs.sellerEmail,
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
            fireSource.userRef().document(toSellerEmail).update(lastTimeMessage)

            val updateTotalRead = fireSource.chatMessageRef(newChatParams.chatArgs.chatId)
                .whereEqualTo(ISREAD, false)
                .whereEqualTo(RECEIVER, toSellerEmail)
                .get().await()

            val totalUnread = updateTotalRead.documents.size
            val lastTotal = mapOf(
                TOTAL_UNREAD to totalUnread
            )

            fireSource.userChatsRef(toSellerEmail).document(newChatParams.chatArgs.chatId)
                .update(lastTotal).await()

            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString(), false))
        }
    }.flowOn(Dispatchers.IO)

    override fun userChatList(userEmail: String): Flow<Resource<SellersData>> = flow {
        emit(Resource.Loading())
        try {
            val checkMessage =
                fireSource.chatRef().whereArrayContainsAny(CONNECTIONS, listOf(userEmail))
                    .whereEqualTo(STATUS, CONNECTED).get().await()

            val listChat = mutableListOf<SellerReadData>()
            if (checkMessage.documents.size != 0) {
                listChat.clear()
                for (chat in checkMessage.documents) {
                    val sellerEmail =
                        (chat.get(CONNECTIONS) as List<*>)[SECOND_CONNECTION].toString()
                    val unread = fireSource.userChatsRef(userEmail).document(chat.id).get().await()
                    val seller = fireSource.userRef().document(sellerEmail).get().await()
                    if (unread.data != null) {
                        val chatUser = unread.toObject(ChatUser::class.java)
                        val sellerData = seller.toObject(SellerData::class.java)
                        if (chatUser != null && sellerData != null) {
                            val sellerReadData = SellerReadData(chatUser, sellerData)
                            listChat.add(sellerReadData)
                        }
                    } else {
                        val sellerData = seller.toObject(SellerData::class.java)
                        if (sellerData != null) {
                            val sellerReadData = SellerReadData(ChatUser(), sellerData)
                            listChat.add(sellerReadData)
                        }
                    }
                }
            }
            emit(Resource.Success(listChat))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun getMessage(chatId: String) = callbackFlow<Resource<Messages>> {

        val snapshotListener =
            fireSource.chatMessageRef(chatId).orderBy(TIME_UPDATE)
                .addSnapshotListener { snapshot, error ->
                    val response = if (snapshot != null) {
                        val chats = snapshot.toObjects(Message::class.java)
                        Resource.Success(chats)
                    } else {
                        Resource.Error(error?.message.toString())
                    }
                    trySend(response)
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override suspend fun readMessage(readParams: ReadParams) {
        val updateTotalRead = fireSource.chatMessageRef(readParams.chatId)
            .whereEqualTo(ISREAD, false)
            .whereEqualTo(RECEIVER, readParams.userEmail)
            .get().await()

        updateTotalRead.documents.forEach { doc ->
            fireSource.chatMessageRef(readParams.chatId).document(doc.id).update(
                mapOf(ISREAD to true)
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
                    Resource.Success(true)
                } else {
                    Resource.Error("")
                }
                trySend(response)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override fun getSellerChat(sellerEmail: String) = callbackFlow {
        val snapshotListener =
            fireSource.userRef().document(sellerEmail).addSnapshotListener { snapshot, err ->
                val response = if (snapshot != null) {
                    val sellerData = snapshot.toObject(SellerData::class.java)

                    Resource.Success(sellerData ?: SellerData())
                } else {
                    Resource.Error(err?.message.toString())
                }
                trySend(response)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }
}
