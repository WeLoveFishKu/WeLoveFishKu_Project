package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class InvoiceItem(
    @field:SerializedName("invoice_url")
    val invoiceUrl: String,
)