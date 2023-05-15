package id.fishku.fisherseller.presentation.ui.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.FragmentAccountBinding
import id.fishku.fisherseller.presentation.ui.auth.LoginActivity
import id.fishku.fisherseller.seller.services.SessionManager
import id.fishku.fishersellercore.core.ResourceState
import id.fishku.fishersellercore.model.UserModel
import id.fishku.fishersellercore.util.Constants.DELAY_LOGOUT
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Account fragment
 *
 * @constructor Create empty Account fragment
 */
@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountViewModel by viewModels()

    @Inject
    lateinit var prefs: SessionManager
    @Inject
    lateinit var pop: id.fishku.fishersellercore.view.PopDialog
    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = prefs.getUser()
        binding.tvName.text = data.name
        binding.tvEmail.text = data.email
        binding.tvNo.text = data.phone_number

        binding.ibExit.setOnClickListener {
            logout()
        }
        binding.btnGoogleSign.setOnClickListener {
            isUserLinkedGoogle()
        }
        setUpLinkedLogo()
    }

    private fun logout(){
        showDelDialog()
    }

    private fun setUpLinkedLogo(){
        val user = prefs.getUser()
        viewModel.userIsLinked(user.email!!).observe(viewLifecycleOwner){

            when(it){
                is ResourceState.Loading -> {}
                is ResourceState.Success -> {
                    binding.btnGoogleSign.setCompoundDrawablesWithIntrinsicBounds(R.drawable.google,0,R.drawable.ic_action_close,0)
                }
                is ResourceState.Error -> {
                    binding.btnGoogleSign.setCompoundDrawablesWithIntrinsicBounds(R.drawable.google,0,0,0)
                }
            }
        }
    }


    private fun isUserLinkedGoogle(){
        val user = prefs.getUser()
        viewModel.userIsLinked(user.email!!).observe(viewLifecycleOwner){
            when(it){
                is ResourceState.Loading -> {

                }
                is ResourceState.Success -> {
                    viewModel.deleteUserLinked(user.id!!)
                    viewModel.signOutWithGoogle(googleSignInClient)
                    setUpLinkedLogo()
                }
                is ResourceState.Error -> {
                    signGoogleAuth()
                }
            }

        }

    }

    private fun linkedWithGoogle(email: String?) {
        val user = prefs.getUser()
        val userModel = UserModel(
            id = user.id,
            name = user.name,
            email = user.email,
            link_email = email,
            phone_number = user.phone_number,
            location = user.location,
            roles = user.roles
        )
        viewModel.linkedWithGoogle(userModel)

    }

    private fun signGoogleAuth() {
        val signIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signIntent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.signWithGoogle(result.data).observe(viewLifecycleOwner){
                    val email = it.data?.email
                    linkedWithGoogle(email)
                    setUpLinkedLogo()
                }
            }
        }

    private fun showDelDialog(){
        pop.showDialog(requireContext(),
            positive = {_,_ ->
                lifecycleScope.launchWhenCreated {
                    prefs.logout()
                    viewModel.signOutWithGoogle(googleSignInClient)
                    delay(DELAY_LOGOUT)
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    requireActivity().finish()
                }
            }, negative = {_,_->

            },
            title = getString(R.string.sure_logout),
            subTitle = getString(R.string.sure_logout_sub)
        )
    }

}