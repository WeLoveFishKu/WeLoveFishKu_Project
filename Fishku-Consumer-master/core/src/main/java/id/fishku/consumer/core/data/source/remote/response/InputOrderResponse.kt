package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class InputOrderResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("id_ordering")
	val orderingID: Int? = null,
)
