package id.fishku.consumer.services

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GenerateToken @Inject constructor(
    private val fire: FirebaseMessaging
) {
    suspend fun getToken(): String{
        return fire.token.await()
    }
}