package id.fishku.fishersellercore.core

import id.fishku.fishersellercore.model.MenuModel
import id.fishku.fishersellercore.response.AddResponse
import id.fishku.fishersellercore.response.GenericResponse
import id.fishku.fishersellercore.response.MessageResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Error parser
 *
 * @property retrofit
 * @constructor Create empty Error parser
 */
class ErrorParser @Inject constructor(
     private val retrofit: Retrofit
) {

    /**
     * Converter message error
     *
     * @param error
     * @return
     */
    fun converterMessageError(error: ResponseBody): MessageResponse?{
       val converter: Converter<ResponseBody, MessageResponse> = retrofit
           .responseBodyConverter(MessageResponse::class.java, arrayOfNulls<Annotation>(0))
       return converter.convert(error)
   }

    /**
     * Converter add error
     *
     * @param error
     * @return
     */
    fun converterAddError(error: ResponseBody): AddResponse?{
        val converter: Converter<ResponseBody, AddResponse> = retrofit
            .responseBodyConverter(AddResponse::class.java, arrayOfNulls<Annotation>(0))
        return converter.convert(error)
    }

    /**
     * Converter generic error
     *
     * @param error
     * @return
     */
    fun converterGenericError(error: ResponseBody): GenericResponse<MenuModel>?{
        val converter: Converter<ResponseBody, GenericResponse<MenuModel>> = retrofit
            .responseBodyConverter(GenericResponse::class.java, arrayOfNulls<Annotation>(0))
        return converter.convert(error)
    }
}