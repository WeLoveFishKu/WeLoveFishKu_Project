package id.fishku.fisherseller.notification.di

import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.fishku.fisherseller.notification.BuildConfig.BASE_URL_NOTIF
import id.fishku.fisherseller.notification.BuildConfig.DEBUG
import id.fishku.fisherseller.notification.data.datasource.FirebaseDatasource
import id.fishku.fisherseller.notification.data.datasource.RemoteDataSource
import id.fishku.fisherseller.notification.data.interceptor.JWTInterceptor
import id.fishku.fisherseller.notification.data.remote.NotificationService
import id.fishku.fisherseller.notification.data.repository.RepositoryImpl
import id.fishku.fisherseller.notification.domain.NotifyRepository
import id.fishku.fisherseller.notification.service.GenerateToken
import id.fishku.fishersellercore.core.ErrorParser
import id.fishku.fishersellercore.core.SafeCall
import id.fishku.fishersellercore.di.RetrofitQualifier.*
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
    fun provideMessagingInstance() =
        FirebaseMessaging.getInstance()

    @Provides
    @Singleton
    fun provideTokenFcm(
        fireMessage: FirebaseMessaging
    ) =
        GenerateToken(fireMessage)

    @Provides
    @Singleton
    @ParserNotify
    fun provideErrorParser(@RetrofitNotify retrofit: Retrofit) =
        ErrorParser(retrofit)

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
            .addInterceptor(JWTInterceptor())
            .addInterceptor(interceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @RetrofitNotify
    fun provideRetrofit( @HttpNotify okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL_NOTIF)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNotificationService(@RetrofitNotify retrofit: Retrofit): NotificationService =
        retrofit.create(NotificationService::class.java)

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        safeCall: SafeCall,
        @ParserNotify converter: ErrorParser,
        service: NotificationService
    ) = RemoteDataSource(safeCall, converter, service)

    @Provides
    @Singleton
    fun provideRepository(data: RemoteDataSource, fireData: FirebaseDatasource): NotifyRepository =
        RepositoryImpl(data, fireData)
}