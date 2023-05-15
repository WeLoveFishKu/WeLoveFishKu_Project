package id.fishku.consumer.core.domain.repository

import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.remote.request.NotificationRequest
import id.fishku.consumer.core.data.source.remote.response.NotificationResponse
import id.fishku.consumer.core.domain.model.SellerData
import id.fishku.consumer.core.domain.params.ParamsToken
import kotlinx.coroutines.flow.Flow

interface INotifyRepository {
    suspend fun pushNotification(request: NotificationRequest): Flow<Resource<NotificationResponse>>
    suspend fun updateToken(paramsToken: ParamsToken): Flow<Boolean>
    fun getListenerToken(sellerEmail: String): Flow<String>
}