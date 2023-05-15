package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class FishResponse(
    @field:SerializedName("banyak")
    val amount: Int? = null,

    @field:SerializedName("data")
    val fishes: List<FishItem>? = null,
)