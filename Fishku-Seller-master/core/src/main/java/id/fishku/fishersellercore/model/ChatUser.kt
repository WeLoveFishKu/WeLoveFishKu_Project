package id.fishku.fishersellercore.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

data class ChatUser(
    var connection: String = "",
    var total_unread: Int = 0,
    var lastTime: Timestamp? = null
){
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "connection" to connection,
            "total_unread" to total_unread,
            "lastTime" to lastTime
        )
    }
}

@Parcelize
data class ChatArgs(
    val consumerEmail: String,
    val chatId: String
): Parcelable

data class ConsumerData(
    var id: Int = 0,
    var name: String = "",
    var email: String = "",
    var token: String? = null,
    var lastMessage: String = "",
    var lastTime: Timestamp? = null
)

data class ConsumerReadData(
    val chatUser: ChatUser,
    val consumerData: ConsumerData
)