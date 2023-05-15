package id.fishku.consumer.core.domain.usecase

import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.remote.request.OtpRequest
import id.fishku.consumer.core.data.source.remote.response.DetectionFishResponse
import id.fishku.consumer.core.data.source.remote.response.OtpResponse
import id.fishku.consumer.core.domain.model.Fish
import id.fishku.consumer.core.domain.model.FishType
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface FishUseCase {
    fun getAllFish(): Flow<Resource<List<Fish>>>
    fun searchFishes(query: String): Flow<Resource<List<Fish>>>
    fun getDetailFish(fishID: Int): Flow<Resource<Fish>>
    fun uploadImageDetection(
        fishName: String?,
        image: MultipartBody.Part
    ): Flow<Resource<DetectionFishResponse>>

    fun getListFishDetection(): Flow<Resource<List<FishType>>>

    fun sendCodeOtp(request: OtpRequest): Flow<Resource<OtpResponse>>
}