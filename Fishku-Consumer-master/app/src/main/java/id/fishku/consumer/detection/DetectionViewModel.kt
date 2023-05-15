package id.fishku.consumer.detection

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.remote.response.DetectionFishResponse
import id.fishku.consumer.core.domain.model.FishType
import id.fishku.consumer.core.domain.usecase.FishUseCase
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class DetectionViewModel @Inject constructor(private val fishUseCase: FishUseCase) : ViewModel() {

    private val _detectionResult = MutableLiveData<Resource<DetectionFishResponse>>()
    val detectionResult: LiveData<Resource<DetectionFishResponse>> get() = _detectionResult

    fun uploadImageDetection(fishName: String?, image: MultipartBody.Part) {
        viewModelScope.launch {
            fishUseCase.uploadImageDetection(fishName, image).collect {
                _detectionResult.value = it
            }
        }
    }

    fun getListFishDetection(): LiveData<Resource<List<FishType>>> =
        fishUseCase.getListFishDetection().asLiveData()
}