package id.fishku.fisherseller.seller.data.remote.api.interceptor

import id.fishku.fisherseller.seller.services.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * J w t interceptor
 *
 * @property pref
 * @constructor Create empty J w t interceptor
 */
class JWTInterceptor(private val pref: SessionManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        pref.getUser().token.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        return chain.proceed(requestBuilder.build())
    }
}