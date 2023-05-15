package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class InvoiceResponse(
    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("errors")
    val errors: List<ErrorResponse>? = null,

    @field:SerializedName("data")
    val data: InvoiceItem? = null,
)