package id.fishku.consumer.detection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.core.utils.addPhotoUrl
import id.fishku.consumer.core.utils.convertFishName
import id.fishku.consumer.core.utils.loadFishImage
import id.fishku.consumer.core.utils.showMessage
import id.fishku.consumer.databinding.FragmentFishTypeDialogBinding

@AndroidEntryPoint
class FishTypeDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFishTypeDialogBinding? = null
    private val binding get() = _binding

    private var fishName: String? = null
    private var fishPart: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentFishTypeDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupData()
        setupAction()
    }

    private fun setupData() {
        val name = arguments?.getString(ChooseFishFragment.FISH_NAME)
        val photo = arguments?.getString(ChooseFishFragment.FISH_PHOTO)

        this.fishName = name

        binding?.apply {
            name.let { tvNameFish.text = it?.convertFishName() }
            photo?.let { imgPhotoFish.loadFishImage(it.addPhotoUrl()) }
        }
    }

    private fun setupAction() {
        binding?.btnSelectFish?.setOnClickListener {
            val selectedChip = binding?.cgFishPart?.children
                ?.filter { (it as Chip).isChecked }
                ?.map { (it as Chip).text.toString() }
                ?.firstOrNull()

            if (selectedChip.isNullOrEmpty() || selectedChip.isBlank()) {
                getString(R.string.select_fish_part).showMessage(requireContext())
            } else {
                val part = selectedChip.toString()
                this.fishPart = part
                (parentFragment as ChooseFishFragment).setPartOfFish(fishName, fishPart)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}