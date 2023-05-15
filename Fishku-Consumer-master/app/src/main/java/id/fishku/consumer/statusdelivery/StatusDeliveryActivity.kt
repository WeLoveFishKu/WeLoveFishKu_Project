package id.fishku.consumer.statusdelivery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import id.fishku.consumer.core.ui.TrackOrderAdapter
import id.fishku.consumer.core.utils.DummyData
import id.fishku.consumer.databinding.ActivityStatusDeliveryBinding

class StatusDeliveryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatusDeliveryBinding
    private val trackOrderAdapter: TrackOrderAdapter by lazy { TrackOrderAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
    }

    private fun setupAdapter() {
        trackOrderAdapter.submitList(DummyData.getTrackOrder())
        binding.rvDetailOrder.apply {
            layoutManager = LinearLayoutManager(this@StatusDeliveryActivity)
            setHasFixedSize(true)
            adapter = trackOrderAdapter
        }
    }
}