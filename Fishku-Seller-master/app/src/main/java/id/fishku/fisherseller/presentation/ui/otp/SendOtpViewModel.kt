package id.fishku.fisherseller.presentation.ui.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.fishersellercore.core.Resource
import id.fishku.fisherseller.otp.domain.Repository
import id.fishku.fishersellercore.requests.OtpRequest
import id.fishku.fishersellercore.response.OtpResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendOtpViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {
    private var _otpRes = MutableLiveData<Resource<OtpResponse>>()
    val otpResponse: LiveData<Resource<OtpResponse>> get() = _otpRes

    fun sendOtpCode(request: OtpRequest){
        viewModelScope.launch {
            repository.sendOtpCode(request).collect{
                _otpRes.value = it
            }
        }
    }
}