package id.fishku.fisherseller.otp.domain

import id.fishku.fishersellercore.core.Resource
import id.fishku.fishersellercore.requests.OtpRequest
import id.fishku.fishersellercore.response.OtpResponse
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun sendOtpCode(request: OtpRequest): Flow<Resource<OtpResponse>>
}