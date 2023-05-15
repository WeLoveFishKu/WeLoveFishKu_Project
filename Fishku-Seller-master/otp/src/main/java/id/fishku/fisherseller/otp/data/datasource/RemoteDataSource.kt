package id.fishku.fisherseller.otp.data.datasource

import id.fishku.fishersellercore.core.Resource
import id.fishku.fisherseller.otp.data.remote.OtpApiService
import id.fishku.fishersellercore.core.ErrorParser
import id.fishku.fishersellercore.core.SafeCall
import id.fishku.fishersellercore.requests.OtpRequest
import id.fishku.fishersellercore.response.OtpResponse
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
    private val service: OtpApiService,
){

    fun sendOtpCode(request: OtpRequest): Flow<Resource<OtpResponse>> = flow {
        emit(Resource.loading(null))
        val res = safeCall.enqueue(request, converter::converterMessageError, service::sendOtpCode)
        emit(res)
    }.flowOn(Dispatchers.IO)

}