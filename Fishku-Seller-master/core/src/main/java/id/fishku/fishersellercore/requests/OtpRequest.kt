package id.fishku.fishersellercore.requests

data class OtpRequest (
    val messaging_product: String? = "whatsapp",
    val recipient_type: String? = "individual",
    val to: String,
    val type: String? = "template",
    val template: Template
)
data class Template (
    val name: String? = "otp_template",
    val language: Language? = Language(),
    val components: List<Component>
)
data class Component (
    val type: String? = "body",
    val parameters: List<Parameter>
)

data class Parameter (
    val type: String? = "text",
    val text: String
)
data class Language (
    val code: String? = "id"
)
