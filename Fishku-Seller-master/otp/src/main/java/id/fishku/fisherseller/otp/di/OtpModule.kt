package id.fishku.fisherseller.otp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.fishku.fisherseller.otp.BuildConfig.BASE_URL_OTP
import id.fishku.fisherseller.otp.BuildConfig.DEBUG
import id.fishku.fisherseller.otp.data.datasource.RemoteDataSource
import id.fishku.fisherseller.otp.data.interceptor.JWTInterceptor
import id.fishku.fisherseller.otp.data.remote.OtpApiService
import id.fishku.fisherseller.otp.data.repository.RepositoryImpl
import id.fishku.fisherseller.otp.domain.Repository
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
object OtpModule {

    @Provides
    @Singleton
    @ParserOtp
    fun provideErrorParser(@RetrofitOtp retrofit: Retrofit) =
        ErrorParser(retrofit)

    @Provides
    @Singleton
    @HttpOtp
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
    @RetrofitOtp
    fun provideRetrofit( @HttpOtp okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL_OTP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideOtpApiService(@RetrofitOtp retrofit: Retrofit): OtpApiService =
        retrofit.create(OtpApiService::class.java)

    @Provides
    @Singleton
    fun provideDataSource(
        safeCall: SafeCall,
        @ParserOtp converter: ErrorParser,
        service: OtpApiService
    ) = RemoteDataSource(safeCall, converter, service)

    @Provides
    @Singleton
    fun provideRepository(data: RemoteDataSource): Repository =
        RepositoryImpl(data)

}