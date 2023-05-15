package id.fishku.fisherseller.notification.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import id.fishku.fisherseller.notification.params.ParamsToken
import id.fishku.fishersellercore.model.ConsumerData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDatasource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    companion object{
        const val USERS = "users"
    }
    suspend fun updateToken(paramsToken: ParamsToken): Flow<Boolean> = flow{
        val usersCollect = fireStore.collection(USERS)
       try {
           usersCollect.document(paramsToken.emailToken).update(
               mapOf("token" to paramsToken.token)
           ).await()
           emit(true)
       }catch (e: Exception){
           emit(false)
       }
    }

    fun getListenerUser(consumerEmail: String) = callbackFlow {
        val usersCollect = fireStore.collection(USERS)
        val snapshotListener =
            usersCollect.document(consumerEmail).addSnapshotListener { snapshot, _ ->
                val response = if (snapshot != null) {
                    val consumerData = snapshot.toObject(ConsumerData::class.java)
                    consumerData ?: ConsumerData()
                } else {
                    ConsumerData()
                }
                trySend(response)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }
}