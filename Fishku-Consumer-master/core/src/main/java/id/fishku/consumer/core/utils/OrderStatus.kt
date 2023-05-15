package id.fishku.consumer.core.utils

enum class OrderStatus(val status: String) {
    SENDING("sending"),
    WAITING("waiting"),
    ARRIVE("arrive")
}