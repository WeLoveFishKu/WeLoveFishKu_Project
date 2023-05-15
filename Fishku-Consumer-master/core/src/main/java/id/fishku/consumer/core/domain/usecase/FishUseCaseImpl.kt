package id.fishku.consumer.core.domain.usecase

import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.remote.request.OtpRequest
import id.fishku.consumer.core.data.source.remote.response.DetectionFishResponse
import id.fishku.consumer.core.data.source.remote.response.OtpResponse
import id.fishku.consumer.core.domain.model.Fish
import id.fishku.consumer.core.domain.model.FishType
import id.fishku.consumer.core.domain.repository.IFishRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class FishUseCaseImpl @Inject constructor(private val fishRepository: IFishRepository) :
    FishUseCase {
    override fun getAllFish(): Flow<Resource<List<Fish>>> = fishRepository.getAllFish()

    override fun searchFishes(query: String): Flow<Resource<List<Fish>>> =
        fishRepository.searchFishes(query)

    override fun getDetailFish(fishID: Int): Flow<Resource<Fish>> =
        fishRepository.getDetailFish(fishID)

    override fun uploadImageDetection(
        fishName: String?,
        image: MultipartBody.Part
    ): Flow<Resource<DetectionFishResponse>> = fishRepository.uploadImageDetection(fishName, image)

    override fun getListFishDetection(): Flow<Resource<List<FishType>>> =
        fishRepository.getListFishDetection()

    override fun sendCodeOtp(request: OtpRequest): Flow<Resource<OtpResponse>> =
        fishRepository.sendCodeOtp(request)
}