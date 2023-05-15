package id.fishku.consumer.core.domain.model

data class Filter(
    val filterID: Int,
    val filterName: String,
    val filterType: String,
    val filterValue: String,
)