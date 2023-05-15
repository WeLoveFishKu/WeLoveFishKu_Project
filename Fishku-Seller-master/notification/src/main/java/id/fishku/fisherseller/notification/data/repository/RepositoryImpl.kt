package id.fishku.fisherseller.notification.data.repository

import id.fishku.fisherseller.notification.data.datasource.FirebaseDatasource
import id.fishku.fisherseller.notification.data.datasource.RemoteDataSource
import id.fishku.fisherseller.notification.domain.NotifyRepository
import id.fishku.fisherseller.notification.params.ParamsToken
import id.fishku.fishersellercore.core.Resource
import id.fishku.fishersellercore.model.ConsumerData
import id.fishku.fishersellercore.requests.NotificationRequest
import id.fishku.fishersellercore.response.NotificationResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val dataSource: RemoteDataSource,
    private val fireDatasource: FirebaseDatasource
): NotifyRepository {
    override fun pushNotification(request: NotificationRequest): Flow<Resource<NotificationResponse>> =
        dataSource.pushNotification(request)

    override suspend fun updateToken(paramsToken: ParamsToken): Flow<Boolean> =
        fireDatasource.updateToken(paramsToken)

    override fun getListenerUser(consumerEmail: String): Flow<ConsumerData> =
        fireDatasource.getListenerUser(consumerEmail)
}