package id.fishku.consumer.core.data.repository

import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.firebase.FirebaseDatasource
import id.fishku.consumer.core.data.source.remote.RemoteDataSource
import id.fishku.consumer.core.data.source.remote.network.ApiResponse
import id.fishku.consumer.core.data.source.remote.request.NotificationRequest
import id.fishku.consumer.core.data.source.remote.response.NotificationResponse
import id.fishku.consumer.core.domain.model.SellerData
import id.fishku.consumer.core.domain.params.ParamsToken
import id.fishku.consumer.core.domain.repository.INotifyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotifyRepository @Inject constructor(
    private val dataSource: RemoteDataSource,
    private val fireDatasource: FirebaseDatasource
): INotifyRepository {
    override suspend fun pushNotification(request: NotificationRequest): Flow<Resource<NotificationResponse>> = flow {
        emit(Resource.Loading())
        when(val response = dataSource.pushNotification(request).first()){
            is ApiResponse.Success -> {
                emit(Resource.Success(response.data))
            }
            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))
            is ApiResponse.Empty -> {}
        }
    }

    override suspend fun updateToken(paramsToken: ParamsToken): Flow<Boolean> =
        fireDatasource.updateToken(paramsToken)

    override fun getListenerToken(sellerEmail: String): Flow<String> =
        fireDatasource.getListenerToken(sellerEmail)
}