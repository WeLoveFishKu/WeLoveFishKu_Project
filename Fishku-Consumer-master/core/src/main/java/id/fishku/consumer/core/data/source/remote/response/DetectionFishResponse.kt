package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class DetectionFishResponse(

    @field:SerializedName("prediction")
    val prediction: String? = null
)
