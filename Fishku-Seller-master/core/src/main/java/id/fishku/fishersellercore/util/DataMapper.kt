package id.fishku.fishersellercore.util

import id.fishku.fishersellercore.model.UserModel
import id.fishku.fishersellercore.util.helper.User

object DataMapper {
    fun mapUser(user: User): UserModel =
        UserModel(
            id = user.id,
            name = user.name,
            email = user.email
        )
}