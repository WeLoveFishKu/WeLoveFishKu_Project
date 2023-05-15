package id.fishku.fishersellercore.model

data class DetailOrderModel(
    val consumer_name: String,
    val consumer_phone_number: String,
    val date: String,
    val address: String,
    val note_order: String,
    val fish_name: String,
    val fish_note: String,
    val weight: String,
    val fish_price: String,
    val price: String

)