package id.fishku.consumer.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.fishku.consumer.core.domain.usecase.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun provideAuthUseCase(authUseCaseImpl: AuthUseCaseImpl): AuthUseCase

    @Binds
    @Singleton
    abstract fun provideFishUseCase(fishUseCaseImpl: FishUseCaseImpl): FishUseCase

    @Binds
    @Singleton
    abstract fun provideCartUseCase(cartUseCaseImpl: CartUseCaseImpl): CartUseCase

    @Binds
    @Singleton
    abstract fun provideOrderUseCase(orderUseCaseImpl: OrderUseCaseImpl): OrderUseCase

    @Binds
    @Singleton
    abstract fun provideChatUseCase(chatUseCaseImpl: ChatUsecaseImpl): ChatUsecase

    @Binds
    @Singleton
    abstract fun provideNotificationUseCase(notificationUseCaseImpl: NotifyUseCaseImpl): NotifyUseCase
}