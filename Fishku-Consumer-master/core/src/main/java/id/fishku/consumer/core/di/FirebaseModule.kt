package id.fishku.consumer.core.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.fishku.consumer.core.BuildConfig
import id.fishku.consumer.core.data.source.firebase.FirebaseDatasource
import id.fishku.consumer.core.data.source.remote.network.MainApiService
import id.fishku.consumer.core.data.source.remote.network.ModelApiService
import id.fishku.consumer.core.data.source.remote.network.StorageApiService
import id.fishku.consumer.services.RemoteConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {
    @Provides
    @Singleton
    fun provideFireConfig() = FirebaseRemoteConfig.getInstance()

    @Provides
    @Singleton
    fun provideRemoteConfig(
        remoteConfig: FirebaseRemoteConfig,
    ) = RemoteConfig(remoteConfig)

    @Provides
    @Singleton
    fun provideFireStore() = Firebase.firestore

    @Provides
    @Singleton
    fun provideMessagingInstance() =
        FirebaseMessaging.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDataSource(
        auth: FirebaseAuth,
        fireStore: FirebaseFirestore
    ) = FirebaseDatasource(fireStore, auth)

    @Provides
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideGso(@ApplicationContext context: Context): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("735561181417-mlag98ls0phr5dhgro9ft0cmbrp3v7bk.apps.googleusercontent.com")
            .requestEmail()
            .build()

    @Provides
    @Singleton
    fun provideSignInClient(@ApplicationContext context: Context, gso: GoogleSignInOptions) =
        GoogleSignIn.getClient(context, gso)

}