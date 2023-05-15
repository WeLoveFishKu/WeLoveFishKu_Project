package id.fishku.fisherseller.presentation.ui.order.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import id.fishku.fisherseller.seller.domain.repository.Repository
import id.fishku.fishersellercore.core.Resource
import id.fishku.fishersellercore.response.GetDetailOrderResponse
import javax.inject.Inject

/**
 * Detail order view model
 *
 * @property repo
 * @constructor Create empty Detail order view model
 */
@HiltViewModel
class DetailOrderViewModel @Inject constructor(
    private val repo: Repository
): ViewModel() {
    fun getDetailOrder(idOrder: String): LiveData<Resource<GetDetailOrderResponse>> =
        repo.getDetailOrder(idOrder).asLiveData()
}