package id.fishku.fishersellercore.di

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.fishku.fishersellercore.core.SafeCall
import id.fishku.fishersellercore.data.LocalData
import id.fishku.fishersellercore.services.RemoteConfig
import id.fishku.fishersellercore.view.LottieLoading
import id.fishku.fishersellercore.view.PopDialog
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    @Provides
    @Singleton
    fun provideLocalData(@ApplicationContext context: Context) =
        LocalData(context)
    @Provides
    @Singleton
    fun provideSafeCall() = SafeCall()

    @Provides
    @Singleton
    fun provideLottieLoading() = LottieLoading()

    @Provides
    @Singleton
    fun providePopDialog() = PopDialog()

    @Provides
    @Singleton
    fun provideFireConfig() = FirebaseRemoteConfig.getInstance()

    @Provides
    @Singleton
    fun provideRemoteConfig(
        remoteConfig: FirebaseRemoteConfig,
    ) = RemoteConfig(remoteConfig)
}