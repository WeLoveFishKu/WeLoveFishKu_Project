package id.fishku.consumer.core.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.fishku.consumer.core.BuildConfig
import id.fishku.consumer.core.data.source.remote.network.MainApiService
import id.fishku.consumer.core.data.source.remote.network.ModelApiService
import id.fishku.consumer.core.data.source.remote.network.StorageApiService
import id.fishku.consumer.core.data.source.remote.network.WaApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import id.fishku.consumer.core.di.RetrofitQualifier.*

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
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
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideMainApiService(client: OkHttpClient): MainApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://apis.fishku.id") //BuildConfig.MAIN_BASE_URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(MainApiService::class.java)
    }

    @Provides
    fun provideStorageApiService(client: OkHttpClient): StorageApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://storage.fishku.id") //BuildConfig.STORAGE_BASE_URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(StorageApiService::class.java)
    }

    @Provides
    fun provideModelApiService(client: OkHttpClient): ModelApiService {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://model.fishku.id") //BuildConfig.MODEL_BASE_URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(ModelApiService::class.java)
    }


}