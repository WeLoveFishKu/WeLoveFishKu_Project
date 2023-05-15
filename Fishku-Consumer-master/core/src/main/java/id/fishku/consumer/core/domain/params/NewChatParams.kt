package id.fishku.consumer.core.domain.params

import id.fishku.consumer.core.domain.model.ChatArgs

data class NewChatParams(
    val userEmail: String,
    val chatArgs: ChatArgs,
    val msg: String
)