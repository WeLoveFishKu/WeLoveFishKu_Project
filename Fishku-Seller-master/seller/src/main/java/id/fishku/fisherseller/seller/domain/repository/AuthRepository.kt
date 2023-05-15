package id.fishku.fisherseller.seller.domain.repository

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import id.fishku.fishersellercore.core.ResourceState
import id.fishku.fishersellercore.model.UserModel
import id.fishku.fishersellercore.util.helper.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signWithGoogle(data: Intent?): Flow<ResourceState<User>>
    fun signOutWithGoogle(googleClient: GoogleSignInClient): Flow<String?>
    suspend fun initUser(user: UserModel): Flow<ResourceState<Boolean>>
    fun getUserLinked(emailLink: String): Flow<ResourceState<UserModel>>
    fun getUserIsLinked(emailLink: String): Flow<ResourceState<UserModel>>
    suspend fun deleteUserLinked(userId: String)
}