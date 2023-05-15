package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

class DetailFishItem(
    @field:SerializedName("id_fish")
    val fishID: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("saller_name")
    val sellerName: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("location")
    val location: String,

    @field:SerializedName("weight")
    val weight: Int,

    @field:SerializedName("price")
    val price: Int,

    @field:SerializedName("photo_url")
    val photoUrl: String? = null,
)