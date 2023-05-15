package id.fishku.fisherseller.otp.data.repository

import id.fishku.fisherseller.otp.data.datasource.RemoteDataSource
import id.fishku.fisherseller.otp.domain.Repository
import id.fishku.fishersellercore.core.Resource
import id.fishku.fishersellercore.requests.OtpRequest
import id.fishku.fishersellercore.response.OtpResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class RepositoryImpl @Inject constructor(
    private val data: RemoteDataSource
): Repository {
    override fun sendOtpCode(request: OtpRequest): Flow<Resource<OtpResponse>> =
        data.sendOtpCode(request)

}