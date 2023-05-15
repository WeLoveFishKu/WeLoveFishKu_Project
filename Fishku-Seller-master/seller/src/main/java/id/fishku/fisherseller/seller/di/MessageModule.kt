package id.fishku.fisherseller.seller.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.fishku.fisherseller.seller.data.firebase.FirebaseDatasource
import id.fishku.fisherseller.seller.data.repository.ChatRepositoryImpl
import id.fishku.fisherseller.seller.domain.repository.ChatRepository
import id.fishku.fishersellercore.di.RetrofitQualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MessageModule {

    @Provides
    @Singleton
    fun provideFireStore() = Firebase.firestore

    @Provides
    @Singleton
    @RetrofitQualifier.FireSourceSeller
    fun provideFirebaseDataSource(
        auth: FirebaseAuth,
        fireStore: FirebaseFirestore
    ) = FirebaseDatasource(fireStore, auth)

    @Provides
    @Singleton
    fun provideChatRepository(
        fireSource: FirebaseDatasource
    ): ChatRepository = ChatRepositoryImpl(fireSource)
}

