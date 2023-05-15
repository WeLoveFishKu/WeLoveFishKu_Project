package id.fishku.consumer.core.domain.usecase

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.Login
import id.fishku.consumer.core.domain.model.Register
import id.fishku.consumer.core.domain.model.User
import id.fishku.consumer.core.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AuthUseCaseImpl @Inject constructor(private val authRepository: IAuthRepository) :
    AuthUseCase {
    override fun loginUser(email: String, password: String): Flow<Resource<Login>> =
        authRepository.loginUser(email, password)

    override suspend fun saveSession(token: String, user: User) =
        authRepository.saveSession(token, user)

    override fun getToken(): Flow<String> = authRepository.getToken()

    override fun getId(): Flow<Int> = authRepository.getId()

    override fun getName(): Flow<String> = authRepository.getName()

    override fun getEmail(): Flow<String> = authRepository.getEmail()

    override fun getPhoneNumber(): Flow<String> = authRepository.getPhoneNumber()

    override fun getAddress(): Flow<String> = authRepository.getAddress()

    override suspend fun deleteSession() = authRepository.deleteSession()

    override suspend fun saveActivity() = authRepository.saveActivity()

    override fun getFirstLaunch(): Flow<Boolean> = authRepository.getFirstLaunch()

    override fun registerUser(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        address: String,
    ): Flow<Resource<Register>> =
        authRepository.registerUser(name, email, password, phoneNumber, address)

    override fun signWithGoogle(data: Intent?): Flow<Resource<User>> =
        authRepository.signWithGoogle(data)

    override fun signOutWithGoogle(googleClient: GoogleSignInClient): Flow<String?> =
        authRepository.signOutWithGoogle(googleClient)

    override suspend fun initUser(user: User): Flow<Resource<Boolean>> =
        authRepository.initUser(user)

    override fun getUserLinked(emailLink: String): Flow<Resource<User>> =
        authRepository.getUserLinked(emailLink)

    override fun getUserIsLinked(userEmail: String): Flow<Resource<User>> =
        authRepository.getUserIsLinked(userEmail)

    override suspend fun deleteUserLinked(userId: String) {
        authRepository.deleteUserLinked(userId)
    }

    override fun editProfile(
        consumerID: Int,
        name: RequestBody,
        phoneNumber: RequestBody,
        phone: RequestBody,
        photoUrl: MultipartBody.Part,
    ): Flow<Resource<String>> =
        authRepository.editProfile(consumerID, name, phoneNumber, phone, photoUrl)
}