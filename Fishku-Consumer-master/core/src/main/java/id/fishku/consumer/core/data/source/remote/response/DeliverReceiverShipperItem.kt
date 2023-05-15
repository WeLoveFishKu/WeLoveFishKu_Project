package id.fishku.consumer.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class DeliverReceiverShipperItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("mobile_number")
	val mobileNumber: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("poi")
	val poi: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("insurance")
	val insurance: Boolean? = null,

	@field:SerializedName("goods")
	val goods: String? = null,

	@field:SerializedName("goods_value")
	val goodsValue: String? = null
)