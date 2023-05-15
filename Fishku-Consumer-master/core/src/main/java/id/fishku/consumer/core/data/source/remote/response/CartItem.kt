package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class CartItem(

    @field:SerializedName("id_fish")
    val fishID: Int,

    @field:SerializedName("id_consumer")
    val consumerID: Int,

    @field:SerializedName("notes")
    val notes: String,

    @field:SerializedName("price")
    val price: Int,

    @field:SerializedName("id_cart")
    val cartID: Int,

    @field:SerializedName("fish_name")
    val fishName: String ? = null,

    @field:SerializedName("weight")
    val weight: Int,

    @field:SerializedName("photo_url")
    val photoUrl: String? = null,

    @field:SerializedName("email_seller")
    val sellerEmail: String? = null
)
