package id.fishku.consumer.core.domain.usecase

import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.repository.NotifyRepository
import id.fishku.consumer.core.data.source.remote.request.NotificationRequest
import id.fishku.consumer.core.data.source.remote.response.NotificationResponse
import id.fishku.consumer.core.domain.model.SellerData
import id.fishku.consumer.core.domain.params.ParamsToken
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotifyUseCaseImpl @Inject constructor(
    private val notifyRepository: NotifyRepository
): NotifyUseCase {
    override suspend fun pushNotification(request: NotificationRequest): Flow<Resource<NotificationResponse>> =
        notifyRepository.pushNotification(request)

    override suspend fun updateToken(paramsToken: ParamsToken): Flow<Boolean> =
        notifyRepository.updateToken(paramsToken)

    override fun getListenerToken(sellerEmail: String): Flow<String> =
        notifyRepository.getListenerToken(sellerEmail)
}