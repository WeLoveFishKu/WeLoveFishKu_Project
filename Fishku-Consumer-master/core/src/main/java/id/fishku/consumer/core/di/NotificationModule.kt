package id.fishku.consumer.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.fishku.consumer.core.BuildConfig.DEBUG
import id.fishku.consumer.core.data.source.remote.interceptor.JWTNotifyInterceptor
import id.fishku.consumer.core.data.source.remote.network.NotificationService
import id.fishku.consumer.core.di.RetrofitQualifier.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    @HttpNotify
    fun provideOkHttpClient(
    ): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().setLevel(
            if (DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        )

        return OkHttpClient.Builder()
            .addInterceptor(JWTNotifyInterceptor())
            .addInterceptor(interceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideNotificationService(@HttpNotify client: OkHttpClient): NotificationService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(NotificationService::class.java)
    }

}