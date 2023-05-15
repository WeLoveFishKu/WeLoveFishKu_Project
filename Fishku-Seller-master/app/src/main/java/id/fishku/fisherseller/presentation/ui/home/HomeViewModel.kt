package id.fishku.fisherseller.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.fisherseller.seller.domain.repository.Repository
import id.fishku.fishersellercore.core.Resource
import id.fishku.fishersellercore.model.MenuModel
import id.fishku.fishersellercore.response.GenericResponse
import id.fishku.fishersellercore.response.MessageResponse
import javax.inject.Inject

/**
 * Home view model
 *
 * @property repo
 * @constructor Create empty Home view model
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {

    /**
     * Get list fish
     *
     * @param idSeller
     * @return
     */
    fun getListFish(idSeller: String): LiveData<Resource<GenericResponse<MenuModel>>> =
        repo.getAllFish(idSeller).asLiveData()

    /**
     * Get search fish
     *
     * @param idSeller
     * @param name
     * @return
     * If wanna use search api
     */
    fun getSearchFish(idSeller: String, name:String): LiveData<Resource<GenericResponse<MenuModel>>> =
        repo.getSearchFish(idSeller, name).asLiveData()

    /**
     * Delete menu
     *
     * @param idSeller
     * @return
     */
    fun deleteMenu(idSeller: String): LiveData<Resource<MessageResponse>> = repo.deleteMenu(idSeller).asLiveData()
}