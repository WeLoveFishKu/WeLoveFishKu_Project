package id.fishku.fisherseller.notification.data.interceptor

import id.fishku.fisherseller.notification.BuildConfig.CONTENT_TYPE
import id.fishku.fisherseller.notification.BuildConfig.SERVER_KEY
import okhttp3.Interceptor
import okhttp3.Response

/**
 * J w t interceptor
 *
 * @constructor Create empty J w t interceptor
 */
class JWTInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        requestBuilder.addHeader("Authorization", "key=$SERVER_KEY")
        requestBuilder.addHeader("Content-Type", CONTENT_TYPE)

        return chain.proceed(requestBuilder.build())
    }
}