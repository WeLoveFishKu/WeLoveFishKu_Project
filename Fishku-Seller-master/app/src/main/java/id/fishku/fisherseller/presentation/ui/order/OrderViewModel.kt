package id.fishku.fisherseller.presentation.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.fisherseller.seller.domain.repository.Repository
import id.fishku.fishersellercore.core.Resource
import id.fishku.fishersellercore.model.OrderModel
import id.fishku.fishersellercore.response.GenericResponse
import javax.inject.Inject

/**
 * Order view model
 *
 * @property repo
 * @constructor Create empty Order view model
 */
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {

    fun getOrder(idSeller: String): LiveData<Resource<GenericResponse<OrderModel>>> =
        repo.getOrder(idSeller).asLiveData()
}