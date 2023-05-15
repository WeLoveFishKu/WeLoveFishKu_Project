package id.fishku.fishersellercore.response


data class OtpResponse (
    val messaging_product: String,

    val contacts: List<Contact>,
    val messages: List<Message>
)

data class Contact (
    val input: String,
    val wa_id: String
)

data class Message (
    val id: String
)