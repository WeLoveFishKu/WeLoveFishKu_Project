package id.fishku.consumer.core.data.repository

import id.fishku.consumer.core.data.NetworkBoundResource
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.local.LocalDataSource
import id.fishku.consumer.core.data.source.remote.RemoteDataSource
import id.fishku.consumer.core.data.source.remote.network.ApiResponse
import id.fishku.consumer.core.data.source.remote.request.OtpRequest
import id.fishku.consumer.core.data.source.remote.response.DetectionFishResponse
import id.fishku.consumer.core.data.source.remote.response.FishItem
import id.fishku.consumer.core.data.source.remote.response.OtpResponse
import id.fishku.consumer.core.domain.model.Fish
import id.fishku.consumer.core.domain.model.FishType
import id.fishku.consumer.core.domain.repository.IFishRepository
import id.fishku.consumer.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FishRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) : IFishRepository {
    override fun getAllFish(): Flow<Resource<List<Fish>>> =
        object : NetworkBoundResource<List<Fish>, List<FishItem>>() {
            override fun loadFromDB(): Flow<List<Fish>> =
                localDataSource.getAllFish().map { fishEntities ->
                    fishEntities.map {
                        DataMapper.fishEntityToFish(it)
                    }
                }

            override fun shouldFetch(data: List<Fish>?): Boolean =
                data == null || data.isEmpty()

            override suspend fun createCall(): Flow<ApiResponse<List<FishItem>>> =
                remoteDataSource.getAllFish()

            override suspend fun saveCallResult(data: List<FishItem>) {
                val fishes = data.map {
                    DataMapper.fishResponseToFishEntity(it)
                }
                localDataSource.insertAllFish(fishes)
            }
        }.asFlow()

    override fun searchFishes(query: String): Flow<Resource<List<Fish>>> = flow {
        emit(Resource.Loading())
        when (val response = remoteDataSource.searchFishes(query).first()) {
            is ApiResponse.Success -> {
                val fishes = response.data.map {
                    DataMapper.fishResponseToFish(it)
                }
                emit(Resource.Success(fishes))
            }

            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))

            is ApiResponse.Empty -> {}
        }
    }

    override fun getDetailFish(fishID: Int): Flow<Resource<Fish>> = flow {
        emit(Resource.Loading())
        when (val response = remoteDataSource.getDetailFish(fishID).first()) {
            is ApiResponse.Success -> {
                val fish = DataMapper.detailFishToFish(response.data)
                emit(Resource.Success(fish))
            }

            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))

            is ApiResponse.Empty -> {}
        }
    }

    override fun uploadImageDetection(
        fishName: String?,
        image: MultipartBody.Part
    ): Flow<Resource<DetectionFishResponse>> = flow {
        emit(Resource.Loading())
        when (val response = remoteDataSource.uploadImageDetection(fishName, image).first()) {
            is ApiResponse.Success -> {
                emit(Resource.Success(response.data))
            }

            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))

            is ApiResponse.Empty -> {}
        }
    }

    override fun getListFishDetection(): Flow<Resource<List<FishType>>> = flow {
        emit(Resource.Loading())
        when (val response = remoteDataSource.getListFishDetection().first()) {
            is ApiResponse.Success -> {
                val fishes = response.data.map {
                    DataMapper.fishDetectionToFishType(it)
                }
                emit(Resource.Success(fishes))
            }

            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))

            is ApiResponse.Empty -> {}
        }
    }

    override fun sendCodeOtp(request: OtpRequest): Flow<Resource<OtpResponse>> = flow {
        emit(Resource.Loading())
        when(val response = remoteDataSource.sendCodeOtp(request).first()){
            is ApiResponse.Success -> {
                emit(Resource.Success(response.data))
            }
            is ApiResponse.Empty -> {}
            is ApiResponse.Error -> emit(Resource.Error(response.errorMessage))
        }
    }
}