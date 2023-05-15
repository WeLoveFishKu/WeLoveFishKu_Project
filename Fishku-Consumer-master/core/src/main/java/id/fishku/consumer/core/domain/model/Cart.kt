package id.fishku.consumer.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    val fishID: Int,
    val consumerID: Int,
    val notes: String,
    val price: Int,
    val cartID: Int,
    val fishName: String,
    val weight: Int,
    val photoUrl: String,
    val sellerEmail: String
) : Parcelable