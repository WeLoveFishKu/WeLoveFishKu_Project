package id.fishku.consumer.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.fishku.consumer.core.data.repository.*
import id.fishku.consumer.core.domain.repository.*

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideAuthRepository(authRepository: AuthRepository): IAuthRepository

    @Binds
    abstract fun provideFishRepository(fishRepository: FishRepository): IFishRepository

    @Binds
    abstract fun provideCartRepository(cartRepository: CartRepository): ICartRepository

    @Binds
    abstract fun provideOrderRepository(orderRepository: OrderRepository): IOrderRepository

    @Binds
    abstract fun provideChatRepository(chatRepository: ChatRepository): IChatRepository
}