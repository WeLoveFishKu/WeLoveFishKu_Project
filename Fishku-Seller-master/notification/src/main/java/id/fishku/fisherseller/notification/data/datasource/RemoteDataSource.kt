package id.fishku.fisherseller.notification.data.datasource

import id.fishku.fisherseller.notification.data.remote.NotificationService
import id.fishku.fishersellercore.core.ErrorParser
import id.fishku.fishersellercore.core.Resource
import id.fishku.fishersellercore.core.SafeCall
import id.fishku.fishersellercore.requests.NotificationRequest
import id.fishku.fishersellercore.response.NotificationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Data source
 *
 * @property safeCall
 * @property converter
 * @constructor Create empty Data source
 */
class RemoteDataSource @Inject constructor(
    private val safeCall: SafeCall,
    private val converter: ErrorParser,
    private val service: NotificationService,
){

    fun pushNotification(request: NotificationRequest): Flow<Resource<NotificationResponse>> = flow {
        emit(Resource.loading(null))
        val res = safeCall.enqueue(request, converter::converterMessageError, service::pushNotification)
        emit(res)
    }.flowOn(Dispatchers.IO)

}