package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class OrderDetailItem(

    @field:SerializedName("fish_name")
    val fishName: String,

    @field:SerializedName("weight")
    val weight: Int,

    @field:SerializedName("photo_url")
    val photoUrl: String? = null,

    @field:SerializedName("fish_price")
    val fishPrice: Int,

    @field:SerializedName("status")
    val status: String,
)