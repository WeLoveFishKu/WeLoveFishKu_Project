package id.fishku.consumer.core.domain.repository

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Login
import id.fishku.consumer.core.domain.model.Register
import id.fishku.consumer.core.domain.model.User
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface IAuthRepository {
    fun loginUser(email: String, password: String): Flow<Resource<Login>>

    suspend fun saveSession(token: String, user: User)

    fun getToken(): Flow<String>

    fun getId(): Flow<Int>

    fun getName(): Flow<String>

    fun getEmail(): Flow<String>

    fun getPhoneNumber(): Flow<String>

    fun getAddress(): Flow<String>

    suspend fun deleteSession()

    fun registerUser(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        address: String,
    ): Flow<Resource<Register>>

    fun signWithGoogle(data: Intent?): Flow<Resource<User>>
    fun signOutWithGoogle(googleClient: GoogleSignInClient): Flow<String?>

    suspend fun initUser(user: User): Flow<Resource<Boolean>>
    fun getUserLinked(emailLink: String): Flow<Resource<User>>
    fun getUserIsLinked(userEmail: String): Flow<Resource<User>>
    suspend fun deleteUserLinked(userId: String)

    fun editProfile(
        consumerID: Int,
        name: RequestBody,
        phoneNumber: RequestBody,
        phone: RequestBody,
        photoUrl: MultipartBody.Part,
    ): Flow<Resource<String>>

    suspend fun saveActivity()

    fun getFirstLaunch(): Flow<Boolean>
}