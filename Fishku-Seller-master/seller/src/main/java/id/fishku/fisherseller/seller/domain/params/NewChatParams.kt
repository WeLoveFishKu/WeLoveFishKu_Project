package id.fishku.fisherseller.seller.domain.params

import id.fishku.fishersellercore.model.ChatArgs

data class NewChatParams(
    val userEmail: String,
    val chatArgs: ChatArgs,
    val msg: String
)