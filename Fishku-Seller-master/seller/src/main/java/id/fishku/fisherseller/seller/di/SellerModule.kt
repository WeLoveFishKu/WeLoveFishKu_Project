package id.fishku.fisherseller.seller.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.fishku.fisherseller.seller.BuildConfig.BASE_URL
import id.fishku.fisherseller.seller.BuildConfig.DEBUG
import id.fishku.fisherseller.seller.data.datasource.RemoteDataSource
import id.fishku.fisherseller.seller.data.firebase.FirebaseDatasource
import id.fishku.fisherseller.seller.data.remote.api.get.GetService
import id.fishku.fisherseller.seller.data.remote.api.interceptor.JWTInterceptor
import id.fishku.fisherseller.seller.data.remote.api.post.PostService
import id.fishku.fisherseller.seller.data.remote.api.post.UserService
import id.fishku.fisherseller.seller.data.repository.AuthRepositoryImpl
import id.fishku.fisherseller.seller.data.repository.RepositoryImpl
import id.fishku.fisherseller.seller.domain.repository.AuthRepository
import id.fishku.fisherseller.seller.domain.repository.Repository
import id.fishku.fisherseller.seller.services.SessionManager
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
object SellerModule {

    @Provides
    @Singleton
    @ParserSeller
    fun provideErrorParser(@RetrofitSeller retrofit: Retrofit) =
        ErrorParser(retrofit)

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context) =
        SessionManager(context)


    @Provides
    @Singleton
    @HttpSeller
    fun provideOkHttpClient(
        prefs: SessionManager
    ): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().setLevel(
            if (DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        )
        return OkHttpClient.Builder()
            .addInterceptor(JWTInterceptor(prefs))
            .addInterceptor(interceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @RetrofitSeller
    fun provideRetrofit(@HttpSeller okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideUserService(@RetrofitSeller retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun providePostService(@RetrofitSeller retrofit: Retrofit): PostService =
        retrofit.create(PostService::class.java)

    @Provides
    @Singleton
    fun provideGetService(@RetrofitSeller retrofit: Retrofit): GetService =
        retrofit.create(GetService::class.java)

    @Provides
    @Singleton
    fun provideDataSource(
        safeCall: SafeCall,
        @ParserSeller converter: ErrorParser,
        userService: UserService,
        postService: PostService,
        getService: GetService
    ) = RemoteDataSource(safeCall, converter, userService, postService, getService)

    @Provides
    @Singleton
    fun provideRepository(data: RemoteDataSource): Repository =
        RepositoryImpl(data)

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

    @Provides
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(@FireSourceSeller data: FirebaseDatasource): AuthRepository =
        AuthRepositoryImpl(data)
}

