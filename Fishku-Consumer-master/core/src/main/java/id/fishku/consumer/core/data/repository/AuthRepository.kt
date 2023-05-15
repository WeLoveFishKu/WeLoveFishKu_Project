package id.fishku.consumer.core.data.repository

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.firebase.FirebaseDatasource
import id.fishku.consumer.core.data.source.local.LocalDataSource
import id.fishku.consumer.core.data.source.remote.RemoteDataSource
import id.fishku.consumer.core.data.source.remote.network.ApiResponse
import id.fishku.consumer.core.domain.model.Login
import id.fishku.consumer.core.domain.model.Register
import id.fishku.consumer.core.domain.model.User
import id.fishku.consumer.core.domain.repository.IAuthRepository
import id.fishku.consumer.core.utils.DataMapper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val fireDataSource: FirebaseDatasource
) : IAuthRepository {

    override fun loginUser(email: String, password: String): Flow<Resource<Login>> = flow {
        emit(Resource.Loading())
        when (val apiResponse = remoteDataSource.loginUser(email, password).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.loginResponseToLogin(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.errorMessage))
            }
            is ApiResponse.Empty -> {}
        }
    }

    override suspend fun saveSession(token: String, user: User) {
        val userEntity = DataMapper.userToUserEntity(user)
        localDataSource.saveSession(token, userEntity)
    }

    override fun getToken(): Flow<String> = localDataSource.getToken()

    override fun getId(): Flow<Int> = localDataSource.getId()

    override fun getName(): Flow<String> = localDataSource.getName()

    override fun getEmail(): Flow<String> = localDataSource.getEmail()

    override fun getPhoneNumber(): Flow<String> = localDataSource.getPhoneNumber()

    override fun getAddress(): Flow<String> = localDataSource.getAddress()

    override suspend fun deleteSession() {
        localDataSource.deleteSession()
    }

    override suspend fun saveActivity() {
        localDataSource.saveActivity()
    }

    override fun getFirstLaunch(): Flow<Boolean> = localDataSource.getFirstLaunch()

    override fun registerUser(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        address: String,
    ): Flow<Resource<Register>> = flow {
        emit(Resource.Loading())
        when (val apiResponse =
            remoteDataSource.registerUser(name, email, password, phoneNumber, address).first()) {
            is ApiResponse.Success -> {
                val data = DataMapper.registerResponseToRegister(apiResponse.data)
                emit(Resource.Success(data))
            }
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.errorMessage))
            }
            is ApiResponse.Empty -> {}
        }
    }

    override fun signWithGoogle(data: Intent?): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)

            val signIn = fireDataSource.signWithGoogle(account).await()
            if (signIn.user?.email != null){
                val userSignIn = signIn.user
                val user = User(
                    name = userSignIn?.displayName,
                    email = userSignIn?.email
                )
                emit(Resource.Success(user))
            }

        }catch (e: ApiException){
            emit(Resource.Error(e.message.toString()))
        }
    }

    override fun signOutWithGoogle(googleClient: GoogleSignInClient): Flow<String?> = flow {
        try {
            fireDataSource.signOutWithGoogle()
            googleClient.signOut().await()
            emit(null)
        }catch (e: ApiException){
            emit(e.message.toString())
        }
    }

    override suspend fun initUser(user: User): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val id = user.id!!.toString()
            fireDataSource.linkAuthRef().document(id).set(user.toMapLinked()).await()
            emit(Resource.Success(true))

        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString(), null))
        }
    }

    override fun getUserLinked(emailLink: String): Flow<Resource<User>> = callbackFlow {


            val userLink = fireDataSource.linkAuthRef().whereEqualTo("link_email", emailLink).addSnapshotListener { value, error ->
                if (value != null) {
                    val response = value.documents[0].toObject(User::class.java)
                    trySend(Resource.Success(response!!))
                }else{
                    trySend(Resource.Error(""))
                }

            }
        awaitClose {
            userLink.remove()
        }
    }

    override fun getUserIsLinked(userEmail: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {

            val userLink = fireDataSource.linkAuthRef().whereEqualTo("email", userEmail).get().await()
            val userLinkObject = userLink.documents[0].toObject(User::class.java) ?: User()

            if (userLink.documents.size != 0){
                emit(Resource.Success(userLinkObject))
            }

        }catch (e: Exception){
            emit(Resource.Error(e.message.toString()))
        }
    }



    override suspend fun deleteUserLinked(userId: String) {
        fireDataSource.linkAuthRef().document(userId).delete().await()
    }

    override fun editProfile(
        consumerID: Int,
        name: RequestBody,
        phoneNumber: RequestBody,
        phone: RequestBody,
        photoUrl: MultipartBody.Part,
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when (val apiResponse =
            remoteDataSource.editProfile(consumerID, name, phoneNumber, phone, photoUrl).first()) {
            is ApiResponse.Success -> {
                val data = apiResponse.data.message
                if (data != null) {
                    emit(Resource.Success(data))
                }
            }
            is ApiResponse.Error -> {
                emit(Resource.Error(apiResponse.errorMessage))
            }
            is ApiResponse.Empty -> {}
        }
    }
}