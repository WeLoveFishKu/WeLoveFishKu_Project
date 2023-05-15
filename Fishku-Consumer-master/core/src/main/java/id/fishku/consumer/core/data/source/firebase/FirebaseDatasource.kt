package id.fishku.consumer.core.data.source.firebase

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import id.fishku.consumer.core.domain.model.SellerData
import id.fishku.consumer.core.domain.params.ParamsToken
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDatasource @Inject constructor(
        private val firestore: FirebaseFirestore,
        private val fireAuth: FirebaseAuth
){
    companion object{
        const val USERS = "users"
        const val CHATS = "chats"
        const val MESSAGE = "message"
    }
    fun userRef(): CollectionReference =
        firestore.collection(USERS)

    fun chatRef(): CollectionReference =
        firestore.collection(CHATS)

    fun userChatsRef(userId: String): CollectionReference =
        firestore.collection(USERS).document(userId).collection(CHATS)

    fun chatMessageRef(chatId: String): CollectionReference =
        firestore.collection(CHATS).document(chatId).collection(MESSAGE)

    suspend fun updateToken(paramsToken: ParamsToken): Flow<Boolean> = flow{
        val usersCollect = firestore.collection(USERS)
        try {
            usersCollect.document(paramsToken.emailToken).update(
                mapOf("token" to paramsToken.token)
            ).await()
            emit(true)
        }catch (e: Exception){
            emit(false)
        }
    }

    fun getListenerToken(sellerEmail: String) = callbackFlow {
        val usersCollect = firestore.collection(USERS)
        val snapshotListener =
            usersCollect.document(sellerEmail).addSnapshotListener { snapshot, _ ->
                val response = if (snapshot != null) {
                    val consumerData = snapshot.get("token").toString()
                    consumerData ?: ""
                } else {
                    ""
                }
                trySend(response)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }

    fun signWithGoogle(acct: GoogleSignInAccount) =
        fireAuth.signInWithCredential(GoogleAuthProvider.getCredential(acct.idToken, null))
    fun signOutWithGoogle() = fireAuth.signOut()

    fun linkAuthRef(): CollectionReference =
        firestore.collection("linkedConsumer")
}