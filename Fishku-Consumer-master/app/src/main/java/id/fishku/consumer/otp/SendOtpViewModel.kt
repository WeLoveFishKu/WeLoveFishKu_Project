package id.fishku.consumer.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.remote.request.OtpRequest
import id.fishku.consumer.core.data.source.remote.response.OtpResponse
import id.fishku.consumer.core.domain.usecase.FishUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendOtpViewModel @Inject constructor(
    private val fishUseCase: FishUseCase
): ViewModel() {
    private var _otpRes = MutableLiveData<Resource<OtpResponse>>()
    val otpResponse: LiveData<Resource<OtpResponse>> get() = _otpRes

    fun sendOtpCode(request: OtpRequest){
        viewModelScope.launch {
            fishUseCase.sendCodeOtp(request).collect{
                _otpRes.value = it
            }
        }
    }
}