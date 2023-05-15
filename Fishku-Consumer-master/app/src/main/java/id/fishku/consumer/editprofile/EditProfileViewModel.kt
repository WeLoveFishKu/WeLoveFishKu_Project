package id.fishku.consumer.editprofile

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.usecase.AuthUseCase
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
) : ViewModel() {

    private val _result = MutableLiveData<Resource<String>>()
    val result: LiveData<Resource<String>> get() = _result

    private val getId = authUseCase.getId().asLiveData()

    private fun updateProfile(consumerId: Int, name: RequestBody, phone: RequestBody, address: RequestBody, photo: MultipartBody.Part) =
        authUseCase.editProfile(consumerId, name, phone, address, photo).asLiveData()

    fun editProfile(name: RequestBody, phone: RequestBody, address: RequestBody, photo: MultipartBody.Part) =
        Transformations.switchMap(getId) { updateProfile(it, name, phone, address, photo) }
}