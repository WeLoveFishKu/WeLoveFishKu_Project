package id.fishku.fisherseller.presentation.ui.prediksi

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.ActivityPrediksiBinding
import id.fishku.fisherseller.seller.services.SessionManager
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject


class PrediksiActivity : AppCompatActivity() {

    data class Fish(
        val ikanBandeng: Double,
        val ikanTongkol: Double,
        val ikanKembung: Double,
        val provinsi: String
    )

    private lateinit var binding: ActivityPrediksiBinding
    private lateinit var fishList: List<Fish>
    private lateinit var adapter: PredikAdapter

    @Inject
    lateinit var prefs: SessionManager


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPrediksiBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        fishList = readCSVFile()
        val recyclerView: RecyclerView = findViewById(R.id.listPrediksiRecyclerView)
        adapter = PredikAdapter(fishList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        prefs = SessionManager(this)

        prefs.setKisaran(kisaranHargaIkan())

    }

    private fun readCSVFile(): List<Fish> {
        val fishList = mutableListOf<Fish>()

        try {
            val inputStream = assets.open("fishPredik.csv")
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

                    val fish = Fish(ikanBandeng, ikanTongkol, ikanKembung, provinsi)
                    fishList.add(fish)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return fishList
    }

    // make fun kisaran harga ikan diambil dari rata rata semua data
    private fun kisaranHargaIkan(): String {
        val ikanBandeng = fishList.map { it.ikanBandeng }
        val ikanTongkol = fishList.map { it.ikanTongkol }
        val ikanKembung = fishList.map { it.ikanKembung }

        val rataRataIkanBandeng = ikanBandeng.average()
        val rataRataIkanTongkol = ikanTongkol.average()
        val rataRataIkanKembung = ikanKembung.average()

        val total = rataRataIkanBandeng + rataRataIkanTongkol + rataRataIkanKembung
        val kisaranHarga = total / 3
//        ambil 3 angka di belakang koma dari kisaranHarga
        val formated = String.format("%.3f", kisaranHarga)

        return formated
    }
}