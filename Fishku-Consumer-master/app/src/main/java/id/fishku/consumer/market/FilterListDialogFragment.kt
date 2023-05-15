package id.fishku.consumer.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.fishku.consumer.core.domain.model.Filter
import id.fishku.consumer.core.ui.FilterAdapter
import id.fishku.consumer.databinding.FragmentFilterListDialogBinding

class FilterListDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFilterListDialogBinding? = null
    private val binding get() = _binding

    private val filterAdapter: FilterAdapter by lazy { FilterAdapter(::filterItemClicked) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterListDialogBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
    }

    private fun setupAdapter() {
        binding?.rvFilter.apply {
            this?.layoutManager = LinearLayoutManager(context)
            this?.adapter = filterAdapter
        }
    }

    private fun filterItemClicked(filter: Filter) {
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}