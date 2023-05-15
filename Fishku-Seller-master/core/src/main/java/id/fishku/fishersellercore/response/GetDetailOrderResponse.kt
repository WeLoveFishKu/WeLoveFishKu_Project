package id.fishku.fishersellercore.response

import id.fishku.fishersellercore.model.DetailOrderModel


data class GetDetailOrderResponse(
    val banyak: Int,
    val data: List<DetailOrderModel>
)