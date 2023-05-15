package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class DeliverOrderResponse(

	@field:SerializedName("deliverShipperResponse")
	val shipper: DeliverReceiverShipperItem? = null,

	@field:SerializedName("receiver")
	val receiver: List<DeliverReceiverShipperItem?>? = null,

	@field:SerializedName("payment_type")
	val paymentType: String? = null
)