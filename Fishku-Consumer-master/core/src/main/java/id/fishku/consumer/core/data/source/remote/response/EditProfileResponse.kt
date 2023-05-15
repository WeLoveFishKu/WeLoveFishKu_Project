package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class EditProfileResponse(
    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("errors")
    val errors: List<ErrorResponse>? = null,
)
