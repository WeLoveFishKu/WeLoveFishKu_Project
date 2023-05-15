package id.fishku.consumer.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.fishku.consumer.core.BuildConfig
import id.fishku.consumer.core.data.source.local.datastore.LocalData
import id.fishku.consumer.core.data.source.remote.interceptor.JWTInterceptor
import id.fishku.consumer.core.data.source.remote.network.WaApiService
import okhttp3.OkHttpClient
import id.fishku.consumer.core.di.RetrofitQualifier.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OtpModule {
    @Provides
    @Singleton
    fun provideLocalData(@ApplicationContext context: Context) =
        LocalData(context)

    @Provides
    @HttpOtp
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(
                    if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                )
            )
            .addInterceptor(JWTInterceptor())
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideWaApiService(@HttpOtp client: OkHttpClient): WaApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://graph.facebook.com/v15.0/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(WaApiService::class.java)
    }
}