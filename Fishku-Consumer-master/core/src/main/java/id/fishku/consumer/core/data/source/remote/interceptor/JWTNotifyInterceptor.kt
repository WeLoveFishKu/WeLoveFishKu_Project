package id.fishku.consumer.core.data.source.remote.interceptor

import id.fishku.consumer.core.utils.Constants.CONTENT_TYPE
import id.fishku.consumer.core.utils.Constants.SERVER_KEY
import okhttp3.Interceptor
import okhttp3.Response

/**
 * J w t interceptor
 *
 * @constructor Create empty J w t interceptor
 */
class JWTNotifyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        requestBuilder.addHeader("Authorization", "key=$SERVER_KEY")
        requestBuilder.addHeader("Content-Type", CONTENT_TYPE)

        return chain.proceed(requestBuilder.build())
    }
}