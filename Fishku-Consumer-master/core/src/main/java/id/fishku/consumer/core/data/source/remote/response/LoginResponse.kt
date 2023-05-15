package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("token")
    val token: String? = null,

    @field:SerializedName("data")
    val users: List<UserResponse>? = null,

    @field:SerializedName("errors")
    val errors: List<ErrorResponse>? = null,
)