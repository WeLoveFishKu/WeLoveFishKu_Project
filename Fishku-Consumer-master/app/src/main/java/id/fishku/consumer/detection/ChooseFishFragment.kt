package id.fishku.consumer.detection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.model.FishType
import id.fishku.consumer.core.ui.FishTypeAdapter
import id.fishku.consumer.core.utils.showMessage
import id.fishku.consumer.databinding.FragmentChooseFishBinding

@AndroidEntryPoint
class ChooseFishFragment : Fragment() {

    private var _binding: FragmentChooseFishBinding? = null
    private val binding get() = _binding

    private val detectionViewModel: DetectionViewModel by viewModels()
    private val fishTypeAdapter: FishTypeAdapter by lazy { FishTypeAdapter(::productItemClicked) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentChooseFishBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
    }

    fun setPartOfFish(fishName: String?, fishPart: String?) {
        val toDetectionFish =
            ChooseFishFragmentDirections.actionNavigationChooseFishToDetectionFishActivity(
                fishName,
                fishPart
            )
        view?.findNavController()?.navigate(toDetectionFish)
    }

    private fun setupData() {
        detectionViewModel.getListFishDetection().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding?.apply {
                        loadingSearch.visibility = View.VISIBLE
                        rvFishType.visibility = View.GONE
                        imgEmptySearch.visibility = View.GONE
                        tvEmptySearch.visibility = View.GONE
                        imgErrorSearch.visibility = View.GONE
                        tvErrorSearch.visibility = View.GONE
                    }
                }
                is Resource.Success -> {
                    binding?.loadingSearch?.visibility = View.GONE
                    if (!it.data.isNullOrEmpty()) {
                        fishTypeAdapter.submitList(it.data)
                        setupAdapter()
                        isEmptyResult(false)
                    } else {
                        isEmptyResult(true)
                    }
                }
                is Resource.Error -> {
                    binding?.apply {
                        loadingSearch.visibility = View.GONE
                        imgErrorSearch.visibility = View.VISIBLE
                        tvErrorSearch.visibility = View.VISIBLE
                    }
                    it.message?.showMessage(requireContext())
                }
            }
        }
    }

    private fun setupAdapter() {
        binding?.rvFishType?.apply {
            setHasFixedSize(true)
            adapter = fishTypeAdapter
        }
    }

    private fun isEmptyResult(isEmpty: Boolean) {
        if (!isEmpty) {
            binding?.apply {
                rvFishType.visibility = View.VISIBLE
                imgEmptySearch.visibility = View.GONE
                tvEmptySearch.visibility = View.GONE
            }
        } else {
            binding?.apply {
                rvFishType.visibility = View.GONE
                imgEmptySearch.visibility = View.VISIBLE
                tvEmptySearch.visibility = View.VISIBLE
            }
        }
    }

    private fun showBottomSheet(fishType: FishType) {
        val bundle = Bundle()
        bundle.apply {
            putString(FISH_NAME, fishType.name)
            putString(FISH_PHOTO, fishType.photoUrl)
        }

        val modalBottomSheet = FishTypeDialogFragment()
        modalBottomSheet.arguments = bundle
        modalBottomSheet.show(childFragmentManager, modalBottomSheet.tag)
    }

    private fun productItemClicked(fishType: FishType) {
        showBottomSheet(fishType)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val FISH_NAME = "fish_name"
        const val FISH_PHOTO = "fish_photo"
    }
}