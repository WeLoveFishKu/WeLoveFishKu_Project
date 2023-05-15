package id.fishku.fishersellercore.response

import id.fishku.fishersellercore.model.UserModel


data class LoginResponse(
    val token: String,
    val data: List<UserModel>
)
