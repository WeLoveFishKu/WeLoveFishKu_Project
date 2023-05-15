package id.fishku.fishersellercore.response

data class GenericResponse<T>(
    val banyak: Int,
    val data: List<T>
)
