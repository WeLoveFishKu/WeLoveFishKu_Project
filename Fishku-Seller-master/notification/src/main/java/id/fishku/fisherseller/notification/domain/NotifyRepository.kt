package id.fishku.fisherseller.notification.domain

import id.fishku.fisherseller.notification.params.ParamsToken
import id.fishku.fishersellercore.core.Resource
import id.fishku.fishersellercore.model.ConsumerData
import id.fishku.fishersellercore.requests.NotificationRequest
import id.fishku.fishersellercore.response.NotificationResponse
import kotlinx.coroutines.flow.Flow

interface NotifyRepository {
    fun pushNotification(request: NotificationRequest): Flow<Resource<NotificationResponse>>
    suspend fun updateToken(paramsToken: ParamsToken): Flow<Boolean>
    fun getListenerUser(consumerEmail: String): Flow<ConsumerData>
}