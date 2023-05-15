package id.fishku.fishersellercore.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderModel (
    val id_order: String,
    val date: String,
    val notes: String?,
    val total_price: String,
    val status: String
        ): Parcelable