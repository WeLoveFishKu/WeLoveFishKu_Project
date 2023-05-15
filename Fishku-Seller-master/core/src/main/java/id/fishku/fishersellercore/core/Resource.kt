package id.fishku.fishersellercore.core

import id.fishku.fisherseller.otp.core.Status

/**
 * Resource
 *
 * @param T
 * @property status
 * @property data
 * @property message
 * @constructor Create empty Resource
 */
data class Resource<out T>(val status: Status, val data: T?, val  message: String?){
    companion object{
        fun <T> success(data: T?): Resource<T> = Resource(Status.SUCCESS, data, null)
        fun <T> error(message: String?, data: T? = null) = Resource(Status.ERROR, data, message)
        fun <T> loading(data: T? = null) = Resource(Status.LOADING, data, null)
    }
}

sealed class ResourceState<T>(val data: T?, val message: String? = null) {
    class Success<T>(data: T) : ResourceState<T>(data)
    class Loading<T>(data: T? = null) : ResourceState<T>(data)
    class Error<T>(message: String, data: T? = null) : ResourceState<T>(data, message)
}

