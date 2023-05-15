package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class CartResponse(

    @field:SerializedName("banyak")
    val banyak: Int,

    @field:SerializedName("data")
    val data: List<CartItem>? = null,
)