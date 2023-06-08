package id.fishku.fisherseller.presentation.ui.prediksi

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import java.io.BufferedReader
import java.io.InputStreamReader

class PrediksiModel(context: Context) : ViewModel() {
    private val _fishList = MutableLiveData<List<PrediksiActivity.Fish>>()
    val fishList: MutableLiveData<List<PrediksiActivity.Fish>> = _fishList

    init {
        getFish(context)
    }

    private fun getFish(context: Context): MutableLiveData<List<PrediksiActivity.Fish>> {
        try {
            val inputStream = context.assets.open("fishPredik.csv")
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String?
                // Skip the header line
                reader.readLine()

                while (reader.readLine().also { line = it } != null) {
                    val values = line!!.split(",")
                    val ikanBandeng = values[0].toDouble()
                    val ikanTongkol = values[1].toDouble()
                    val ikanKembung = values[2].toDouble()
                    val provinsi = values[3]

                    val fish =
                        PrediksiActivity.Fish(ikanBandeng, ikanTongkol, ikanKembung, provinsi)
                    _fishList.value?.plus(fish)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return _fishList
    }

}
