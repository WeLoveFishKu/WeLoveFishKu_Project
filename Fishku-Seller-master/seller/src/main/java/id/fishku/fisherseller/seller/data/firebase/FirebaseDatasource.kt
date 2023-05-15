package id.fishku.fisherseller.seller.data.firebase

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class FirebaseDatasource @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val fireAuth: FirebaseAuth
){
    fun userRef(): CollectionReference =
        fireStore.collection("users")

    fun chatRef(): CollectionReference =
        fireStore.collection("chats")

    fun userChatsRef(userId: String): CollectionReference =
        fireStore.collection("users").document(userId).collection("chats")

    fun chatMessageRef(chatId: String): CollectionReference =
        fireStore.collection("chats").document(chatId).collection("message")

    fun signWithGoogle(acct: GoogleSignInAccount) =
        fireAuth.signInWithCredential(GoogleAuthProvider.getCredential(acct.idToken, null))
    fun signOutWithGoogle() = fireAuth.signOut()

    fun linkAuthRef(): CollectionReference =
        fireStore.collection("linkedSeller")
}