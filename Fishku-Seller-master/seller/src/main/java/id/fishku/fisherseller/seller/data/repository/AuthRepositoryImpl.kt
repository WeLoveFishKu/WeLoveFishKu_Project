package id.fishku.fisherseller.seller.data.repository

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import id.fishku.fisherseller.seller.data.firebase.FirebaseDatasource
import id.fishku.fisherseller.seller.domain.repository.AuthRepository
import id.fishku.fishersellercore.core.ResourceState
import id.fishku.fishersellercore.model.UserModel
import id.fishku.fishersellercore.util.helper.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val fireSource: FirebaseDatasource
) : AuthRepository {
    override fun signWithGoogle(data: Intent?): Flow<ResourceState<User>> = flow {
        emit(ResourceState.Loading())
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)

            val signIn = fireSource.signWithGoogle(account).await()
            if (signIn.user?.email != null) {
                val userSignIn = signIn.user
                val user = User(
                    name = userSignIn?.displayName,
                    email = userSignIn?.email,
                )
                emit(ResourceState.Success(user))
            }

        } catch (e: ApiException) {
            emit(ResourceState.Error(e.message.toString()))
        }
    }

    override fun signOutWithGoogle(googleClient: GoogleSignInClient): Flow<String?> = flow {
        try {
            fireSource.signOutWithGoogle()
            googleClient.signOut().await()
            emit(null)
        } catch (e: ApiException) {
            emit(e.message.toString())
        }
    }

    override suspend fun initUser(user: UserModel): Flow<ResourceState<Boolean>> = flow {
        emit(ResourceState.Loading())
        try {
            val id = user.id!!
            fireSource.linkAuthRef().document(id).set(user.toMapLinked()).await()
            emit(ResourceState.Success(true))

        } catch (e: Exception) {
            emit(ResourceState.Error(e.message.toString(), null))
        }
    }

    override fun getUserLinked(emailLink: String): Flow<ResourceState<UserModel>> = flow {
        emit(ResourceState.Loading())
        try {

           val userLink = fireSource.linkAuthRef().whereEqualTo("link_email", emailLink).get().await()
            val userLinkObject = userLink.documents[0].toObject(UserModel::class.java) ?: UserModel()

            if (userLink.documents.size != 0){
                emit(ResourceState.Success(userLinkObject))
            }

        }catch (e: Exception){
            emit(ResourceState.Error(e.message.toString()))
        }
    }

    override fun getUserIsLinked(emailLink: String): Flow<ResourceState<UserModel>> = flow {
        emit(ResourceState.Loading())
        try {

            val userLink = fireSource.linkAuthRef().whereEqualTo("email", emailLink).get().await()
            val userLinkObject = userLink.documents[0].toObject(UserModel::class.java) ?: UserModel()

            if (userLink.documents.size != 0){
                emit(ResourceState.Success(userLinkObject))
            }

        }catch (e: Exception){
            emit(ResourceState.Error(e.message.toString()))
        }
    }



    override suspend fun deleteUserLinked(userId: String) {
        fireSource.linkAuthRef().document(userId).delete().await()
    }
}