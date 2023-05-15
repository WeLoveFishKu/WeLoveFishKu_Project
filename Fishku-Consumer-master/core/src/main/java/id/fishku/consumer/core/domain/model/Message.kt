package id.fishku.consumer.core.domain.model

import com.google.firebase.Timestamp

data class Message(
    var receiver: String? = null,
    var sender: String? = null,
    var msg: String? = null,
    var read: Boolean? = null,
    var timeUpdate: Timestamp? = null
){
    fun toMap(): Map<String, Any?> =
        mapOf(
            "receiver" to receiver,
            "sender" to sender,
            "msg" to msg,
            "read" to read,
            "timeUpdate" to timeUpdate
        )
}
