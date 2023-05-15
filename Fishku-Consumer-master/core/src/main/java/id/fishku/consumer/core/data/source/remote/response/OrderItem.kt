package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class OrderItem(
    @field:SerializedName("id_ordering")
    val orderingID: Int,

    @field:SerializedName("date")
    val date: String,

    @field:SerializedName("total_price")
    val totalPrice: Int,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("invoice_url")
    val invoiceUrl: String?,
)