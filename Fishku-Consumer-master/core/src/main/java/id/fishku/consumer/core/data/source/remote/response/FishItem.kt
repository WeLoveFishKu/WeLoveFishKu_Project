package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

class FishItem(
    @field:SerializedName("id_fish")
    val fishID: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("price")
    val price: Int,

    @field:SerializedName("location")
    val location: String? = null,

    @field:SerializedName("photo_url")
    val photoUrl: String? = null,
)