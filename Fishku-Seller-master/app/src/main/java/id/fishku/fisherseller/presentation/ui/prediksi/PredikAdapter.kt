package id.fishku.fisherseller.presentation.ui.prediksi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.fishku.fisherseller.R

class PredikAdapter(private val fishList: List<PrediksiActivity.Fish>) :
    RecyclerView.Adapter<PredikAdapter.FishViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_predik, parent, false)
        return FishViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FishViewHolder, position: Int) {
        val currentFish = fishList[position]
        holder.bind(currentFish)
    }

    override fun getItemCount(): Int {
        return fishList.size
    }

    inner class FishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ikanBandengTextView: TextView = itemView.findViewById(R.id.ikanBandengTextView)
        private val ikanTongkolTextView: TextView = itemView.findViewById(R.id.ikanTongkolTextView)
        private val ikanKembungTextView: TextView = itemView.findViewById(R.id.ikanKembungTextView)
        private val provinsiTextView: TextView = itemView.findViewById(R.id.provinsiTextView)

        fun bind(fish: PrediksiActivity.Fish) {

            val formatIkanBandeng = String.format("%.3f", fish.ikanBandeng)
            val formatIkanTongkol = String.format("%.3f", fish.ikanTongkol)
            val formatIkanKembung = String.format("%.3f", fish.ikanKembung)

            ikanBandengTextView.text = "Ikan Bandeng: Rp $formatIkanBandeng"
            ikanTongkolTextView.text = "Ikan Tongkol: Rp $formatIkanTongkol"
            ikanKembungTextView.text = "Ikan Kembung: Rp $formatIkanKembung"
            provinsiTextView.text = "Provinsi ${fish.provinsi}"
        }
    }
}