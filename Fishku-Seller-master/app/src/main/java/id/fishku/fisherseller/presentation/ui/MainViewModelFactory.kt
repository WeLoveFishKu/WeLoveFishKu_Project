package id.fishku.fisherseller.presentation.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.fishku.fisherseller.presentation.ui.weathers.WeatherModel
import id.fishku.fisherseller.seller.services.SessionManager

class MainViewModelFactory(private val context: Context):
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
