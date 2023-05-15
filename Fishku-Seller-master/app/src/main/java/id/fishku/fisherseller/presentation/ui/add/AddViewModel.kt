package id.fishku.fisherseller.presentation.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.fishersellercore.core.Resource
import id.fishku.fisherseller.seller.domain.repository.Repository
import id.fishku.fishersellercore.requests.AddRequest
import id.fishku.fishersellercore.response.AddResponse
import id.fishku.fishersellercore.response.MessageResponse
import java.io.File
import javax.inject.Inject

/**
 * Add view model
 *
 * @property repo
 * @constructor Create empty Add view model
 */
@HiltViewModel
class AddViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {
    fun postMenu(request: AddRequest): LiveData<Resource<AddResponse>> =
        repo.postMenu(request).asLiveData()

    fun editMenu(idFish: String, request: AddRequest): LiveData<Resource<MessageResponse>> = repo.editMenu(idFish, request).asLiveData()
    fun uploadImage(url: String, file: File): LiveData<Resource<MessageResponse>> = repo.uploadImage(url, file).asLiveData()
}