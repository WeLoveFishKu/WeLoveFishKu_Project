package id.fishku.consumer.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val orderingID: Int,
    val date: String,
    val priceTotal: Int,
    val status: String,
    val invoiceUrl: String?,
) : Parcelable