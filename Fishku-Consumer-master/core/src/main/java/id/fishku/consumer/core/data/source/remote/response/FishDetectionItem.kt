package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class FishDetectionItem(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("photo")
    val photo: String? = null,
)
