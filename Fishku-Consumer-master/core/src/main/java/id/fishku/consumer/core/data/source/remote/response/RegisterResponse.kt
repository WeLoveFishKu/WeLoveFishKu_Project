package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("errors")
    val errors: List<ErrorResponse>? = null,
)