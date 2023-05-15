package id.fishku.consumer.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.local.datastore.LocalData
import id.fishku.consumer.core.domain.model.User
import id.fishku.consumer.databinding.FragmentAccountBinding
import id.fishku.consumer.editprofile.EditProfileActivity
import id.fishku.consumer.order.TabOrderAdapter
import id.fishku.consumer.setting.SettingActivity
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment(), Toolbar.OnMenuItemClickListener {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding
    private val accountViewModel: AccountViewModel by viewModels()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var prefs: LocalData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProfile()
        setupTabOrder()
        setupAction()

        binding!!.btnGoogleSign.setOnClickListener {
            isUserLinkedGoogle()
        }
        setUpLinkedLogo()
    }

    private fun setUpLinkedLogo(){
        val user = prefs.getDataUser()
        accountViewModel.userIsLinked(user.email!!).observe(viewLifecycleOwner){

            when(it){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    binding!!.btnGoogleSign.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_google,0,R.drawable.ic_action_close,0)
                }
                is Resource.Error -> {
                    binding!!.btnGoogleSign.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_google,0,0,0)
                }
            }
        }
    }

    private fun isUserLinkedGoogle(){
        val user = prefs.getDataUser()
        accountViewModel.userIsLinked(user.email!!).observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    accountViewModel.deleteUserLinked(user.id.toString())
                    accountViewModel.signOutWithGoogle(googleSignInClient)
                    setUpLinkedLogo()
                }
                is Resource.Error -> {
                    signGoogleAuth()
                }
            }

        }

    }

    private fun linkedWithGoogle(email: String?) {
        val user = prefs.getDataUser()
        val userModel = User(
            id = user.id,
            name = user.name,
            email = user.email,
            link_email = email,
            phoneNumber = user.phoneNumber,
            address = user.address,
            token = user.token
        )
        accountViewModel.linkedWithGoogle(userModel)

    }

    private fun signGoogleAuth() {
        val signIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signIntent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                accountViewModel.signWithGoogle(result.data).observe(viewLifecycleOwner){
                    val email = it.data?.email
                    linkedWithGoogle(email)
                    setUpLinkedLogo()
                }
            }
        }

    private fun initProfile() {
        accountViewModel.getName().observe(viewLifecycleOwner) { name ->
            binding?.tvAccountName?.text = name
        }

        accountViewModel.getEmail().observe(viewLifecycleOwner) { email ->
            binding?.tvAccountEmail?.text = email
        }

        accountViewModel.getPhoneNumber().observe(viewLifecycleOwner) { phoneNumber ->
            binding?.tvAccountPhoneNumber?.text = phoneNumber
        }
    }

    private fun setupTabOrder() {
        val tabOrderAdapter = TabOrderAdapter(activity as AppCompatActivity)
        val viewPager2 = binding?.viewPagerOrder
        viewPager2?.adapter = tabOrderAdapter
        val tabs = binding?.tabLayoutOrder
        if (tabs != null && viewPager2 != null) {
            TabLayoutMediator(tabs, viewPager2) { tab, position ->
                tab.text = resources.getText(TAB_TITLES[position])
            }.attach()
        }
    }

    private fun setupAction() {
        binding?.toolbarAccount?.setOnMenuItemClickListener(this)
        binding?.btnEditProfile?.setOnClickListener {
            val editProfileIntent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(editProfileIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_order,
            R.string.tab_order_history
        )
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.btn_setting -> {
                val settingIntent = Intent(requireContext(), SettingActivity::class.java)
                startActivity(settingIntent)

                return true
            }
            else -> false
        }
    }
}