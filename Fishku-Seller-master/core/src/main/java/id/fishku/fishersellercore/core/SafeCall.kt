package id.fishku.fishersellercore.core

import id.fishku.fishersellercore.model.MenuModel
import id.fishku.fishersellercore.response.AddResponse
import id.fishku.fishersellercore.response.GenericResponse
import id.fishku.fishersellercore.response.MessageResponse
import id.fishku.fishersellercore.util.Constants.TIMEOUT_ERROR
import id.fishku.fishersellercore.util.Constants.UNKNOWN_ERROR
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.net.SocketTimeoutException

class SafeCall {
    suspend fun <T> enqueue(
        converter: (ResponseBody) -> GenericResponse<MenuModel>?,
        call: suspend () -> Response<T>
    ): Resource<T> =
        try {
            val res = call()
            val body = res.body()
            val errorBody = res.errorBody()

            if (res.isSuccessful && body != null){
                Resource.success(body)
            }else if (errorBody != null){
                val parserError = converter(errorBody)
                Resource.error(parserError?.banyak.toString(), null)
            }else{
                Resource.error(UNKNOWN_ERROR, null)
            }
        }catch (e: Exception){
            when(e){
                is SocketTimeoutException -> Resource.error(TIMEOUT_ERROR, null)
                else -> Resource.error(UNKNOWN_ERROR, null)
            }
        }

    suspend fun <T,R> enqueue(
        req: T,
        converter: (ResponseBody) -> MessageResponse?,
        call: suspend (T) -> Response<R>
    ): Resource<R> =
        try {
            val res = call(req)
            val body = res.body()
            val errorBody = res.errorBody()

            if (res.isSuccessful && body != null){
                Resource.success(body)
            }else if (errorBody != null){
                val parsedError = converter(errorBody)
                Resource.error(parsedError?.message.toString(), null)
            }else{
                Resource.error(UNKNOWN_ERROR, null)
            }
        }catch (e: Exception){
            Timber.tag("safe").e(e.toString())
            when(e){
                is SocketTimeoutException -> Resource.error(TIMEOUT_ERROR, null)
                else -> Resource.error(UNKNOWN_ERROR, null)
            }
        }
    suspend fun <T,R> enqueueA(
        req: T,
        converter: (ResponseBody) -> GenericResponse<MenuModel>?,
        call: suspend (T) -> Response<R>
    ): Resource<R> =
        try {
            val res = call(req)
            val body = res.body()
            val errorBody = res.errorBody()

            if (res.isSuccessful && body != null){
                Resource.success(body)
            }else if (errorBody != null){
                val parsedError = converter(errorBody)
                Resource.error(parsedError?.banyak.toString(), null)
            }else{
                Resource.error(UNKNOWN_ERROR, null)
            }
        }catch (e: Exception){
            Timber.tag("safe").e(e.toString())
            when(e){
                is SocketTimeoutException -> Resource.error(TIMEOUT_ERROR, null)
                else -> Resource.error(UNKNOWN_ERROR, null)
            }
        }

    suspend fun <T, U, R> enqueue(
        req1: T,
        req2: U,
        converter: (ResponseBody) -> GenericResponse<MenuModel>?,
        call: suspend (T, U) -> Response<R>
    ): Resource<R> =
        try {
            val res = call(req1, req2)
            val body = res.body()
            val errorBody = res.errorBody()

            if (res.isSuccessful && body != null) {
                Resource.success(body)
            } else if (errorBody != null) {
                val parsedError = converter(errorBody)
                Resource.error(parsedError?.banyak.toString(), null)
            } else {
                Resource.error(UNKNOWN_ERROR, null)
            }
        } catch (e: Exception) {
            when (e) {
                is SocketTimeoutException -> Resource.error(TIMEOUT_ERROR, null)
                else -> Resource.error(UNKNOWN_ERROR, null)
            }
        }


    suspend fun <T,U,R> enqueueImage(
        req1: T,
        req2: U,
        converter: (ResponseBody) -> MessageResponse?,
        call: suspend (T,U) -> Response<R>
    ): Resource<R> =
        try {
            val res = call(req1, req2)
            val body = res.body()
            val errorBody = res.errorBody()

            if (res.isSuccessful && body != null){
                Resource.success(body)
            }else if (errorBody != null){
                val parsedError = converter(errorBody)
                Resource.error(parsedError?.message.toString(), null)
            }else{
                Resource.error(UNKNOWN_ERROR, null)
            }
        }catch (e: Exception){
            when(e){
                is SocketTimeoutException -> Resource.error(TIMEOUT_ERROR, null)
                else -> Resource.error(UNKNOWN_ERROR, null)
            }
        }

    suspend fun <T,U,S,V,W,X,R> enqueueAdd(
        req1: T,
        req2: U,
        req3: S,
        req4: V,
        req5: W,
        req6: X,
        converter: (ResponseBody) -> AddResponse?,
        call: suspend (T,U,S,V,W,X) -> Response<R>
    ): Resource<R> =
        try {
            val res = call(req1, req2,req3,req4, req5, req6)
            val body = res.body()
            val errorBody = res.errorBody()

            if (res.isSuccessful && body != null){
                Resource.success(body)
            }else if (errorBody != null){
                val parsedError = converter(errorBody)
                Resource.error(parsedError?.message.toString(), null)
            }else{
                Resource.error("try", null)
            }
        }catch (e: Exception){
            Timber.tag("catch").e(e.message.toString())
            when(e){
                is SocketTimeoutException -> Resource.error(TIMEOUT_ERROR, null)
                else -> Resource.error("catch", null)
            }
        }
    suspend fun <T,U,S,V,W,R> enqueueEdt(
        req1: T,
        req2: U,
        req3: S,
        req4: V,
        req5: W,
        converter: (ResponseBody) -> AddResponse?,
        call: suspend (T,U,S,V,W) -> Response<R>
    ): Resource<R> =
        try {
            val res = call(req1, req2,req3,req4, req5)
            val body = res.body()
            val errorBody = res.errorBody()

            if (res.isSuccessful && body != null){
                Resource.success(body)
            }else if (errorBody != null){
                val parsedError = converter(errorBody)
                Resource.error(parsedError?.message.toString(), null)
            }else{
                Resource.error("try", null)
            }
        }catch (e: Exception){
            Timber.tag("catch").e(e.message.toString())
            when(e){
                is SocketTimeoutException -> Resource.error(TIMEOUT_ERROR, null)
                else -> Resource.error("catch", null)
            }
        }
}