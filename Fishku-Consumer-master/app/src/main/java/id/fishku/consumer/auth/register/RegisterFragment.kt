package id.fishku.consumer.auth.register

import android.os.Bundle
import android.util.Patterns.EMAIL_ADDRESS
import android.util.Patterns.PHONE
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.local.datastore.LocalData
import id.fishku.consumer.core.utils.showError
import id.fishku.consumer.core.utils.showMessage
import id.fishku.consumer.databinding.FragmentRegisterBinding
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private val registerViewModel: RegisterViewModel by viewModels()

    @Inject
    lateinit var saveToLocal: LocalData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()
        registerResult()
    }

    private fun setupAction() {
        val nomor = saveToLocal.getNumber()
        binding?.edtPhoneRegister?.setText(nomor)
        binding?.apply {
            btnRegister.setOnClickListener(this@RegisterFragment)
            btnLoginHere.setOnClickListener(this@RegisterFragment)
        }
    }

    private fun registerResult() {
        registerViewModel.result.observe(this) {
            when (it) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    it.data?.message?.showMessage(requireContext())
                    binding?.root?.findNavController()
                        ?.navigate(R.id.action_registerFragment_to_loginFragment)
                }
                is Resource.Error -> {
                    showLoading(false)
                    it.message?.showMessage(requireContext())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_register -> registerHandler()
            R.id.btn_login_here -> v.findNavController()
                .navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun registerHandler() {
        if (!isFormValid()) return

        val name = binding?.edtNameRegister?.text.toString()
        val email = binding?.edtEmailRegister?.text.toString()
        val password = binding?.edtPasswordRegister?.text.toString()
        val phoneNumber = binding?.edtPhoneRegister?.text.toString()

        registerViewModel.registerUser(name, email, password, phoneNumber)
    }

    private fun isFormValid(): Boolean {
        val name = binding?.edtNameRegister?.text.toString()
        val email = binding?.edtEmailRegister?.text.toString()
        val password = binding?.edtPasswordRegister?.text.toString()
        val phoneNumber = binding?.edtPhoneRegister?.text.toString()

        binding?.tilNameRegister?.apply {
            if (name.isEmpty()) {
                showError(true, context.getString(R.string.name_cannot_be_empty))
            } else {
                showError(false)
            }
        }

        binding?.tilEmailRegister?.apply {
            if (email.isEmpty()) {
                showError(true, getString(R.string.email_cannot_be_empty))
            } else {
                if (!EMAIL_ADDRESS.matcher(email).matches()) {
                    showError(true, getString(R.string.invalid_email_format))
                } else {
                    showError(false)
                }
            }
        }

        binding?.tilPasswordRegister?.apply {
            if (password.isEmpty()) {
                showError(true, getString(R.string.password_cannot_be_empty))
            } else {
                showError(false)
            }
        }

        binding?.tilPhoneRegister?.apply {
            if (phoneNumber.isEmpty()) {
                showError(true, getString(R.string.phone_cannot_be_empty))
            } else {
                if (!PHONE.matcher(phoneNumber).matches()) {
                    showError(true, getString(R.string.invalid_phone_format))
                } else {
                    showError(false)
                }
            }
        }

        return binding?.tilNameRegister?.isErrorEnabled == false &&
                binding?.tilEmailRegister?.isErrorEnabled == false &&
                binding?.tilPasswordRegister?.isErrorEnabled == false &&
                binding?.tilPhoneRegister?.isErrorEnabled == false
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding?.apply {
                loadingRegister.visibility = View.VISIBLE
                btnLoginHere.isClickable = false
                btnRegister.isClickable = false
            }
        } else {
            binding?.apply {
                loadingRegister.visibility = View.GONE
                btnLoginHere.isClickable = true
                btnRegister.isClickable = true
            }
        }
    }
}