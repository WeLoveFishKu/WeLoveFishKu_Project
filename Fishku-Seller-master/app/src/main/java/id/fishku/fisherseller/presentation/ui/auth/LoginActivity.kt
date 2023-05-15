package id.fishku.fisherseller.presentation.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.ActivityLoginBinding
import id.fishku.fisherseller.notification.service.GenerateToken
import id.fishku.fisherseller.otp.core.Status
import id.fishku.fisherseller.presentation.ui.DashboardActivity
import id.fishku.fisherseller.presentation.ui.otp.SendOTPActivity
import id.fishku.fisherseller.seller.services.SessionManager
import id.fishku.fisherseller.services.FirebaseService
import id.fishku.fishersellercore.core.ResourceState
import id.fishku.fishersellercore.data.LocalData
import id.fishku.fishersellercore.model.UserModel
import id.fishku.fishersellercore.requests.LoginRequest
import id.fishku.fishersellercore.response.LoginResponse
import id.fishku.fishersellercore.util.DataMapper
import id.fishku.fishersellercore.util.helper.OtpArgs
import id.fishku.fishersellercore.util.helper.User
import id.fishku.fishersellercore.util.mySnackBar
import id.fishku.fishersellercore.util.textTrim
import id.fishku.fishersellercore.view.LottieLoading
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val NUMBER = "number"
    }

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var session: SessionManager

    @Inject
    lateinit var loading: LottieLoading

    @Inject
    lateinit var saveToLocal: LocalData

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var getToken: GenerateToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)


        binding.btnLogin.setOnClickListener(this)
        binding.tvLogin.setOnClickListener(this)
        binding.btnGoogleSign.setOnClickListener {
            signGoogleAuth()

        }
        binding.edtEmail.addTextChangedListener(afterTextChangedListener)
        binding.edtPw.addTextChangedListener(afterTextChangedListener)

        getTokenFcm()
        signResult()
    }

    private fun signResult() {
        viewModel.user.observe(this) {
            when (it) {
                is ResourceState.Loading -> {
                }
                is ResourceState.Success -> {
                    userLinked(it.data?.email)
                }
                is ResourceState.Error -> {
                }
            }
        }
    }

    private fun userLinked(emailLink: String?){
        viewModel.userLinked(emailLink!!).observe(this){
            when(it){
                is ResourceState.Loading -> {}
                is ResourceState.Success -> {
                    saveToSession(it.data)
                }
                is ResourceState.Error -> {
                    binding.root.mySnackBar(getString(R.string.login_to_linked))
                    viewModel.logoutGoogle(googleSignInClient)
                }
            }
        }
    }

    private fun saveToSession(data: UserModel?) {
        val userData = User(
            id = data?.id,
            name = data?.name,
            email = data?.email,
            phone_number = data?.phone_number,
            location = data?.location,
            roles = data?.roles,
            token = data?.token,
        )
        session.setUser(userData)
        session.setLogin(true)
        startActivity(Intent(this, DashboardActivity::class.java))
    }

    private fun signGoogleAuth() {
        val signIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signIntent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.signWithGoogle(result.data)
            }
        }

    private fun getTokenFcm() {
        lifecycleScope.launchWhenCreated {
            FirebaseService.sharedPref = saveToLocal
            val token = getToken.getToken()
            saveToLocal.setTokenFcm(token)
            FirebaseService.token = token
        }
    }

    private fun sendRequestLogin() {
        val email = binding.edtEmail.textTrim()
        val password = binding.edtPw.textTrim()

        val request = LoginRequest(email, password)

        loginAuth(request)
    }

    private fun loginAuth(request: LoginRequest) {
        viewModel.login(request).observe(this) { res ->
            when (res.status) {
                Status.LOADING -> loading.start(this)
                Status.ERROR -> {
                    loading.stop()
                    binding.root.mySnackBar(getString(R.string.wrong_auth))
                }
                Status.SUCCESS -> {
                    loading.stop()
                    saveLoginData(res.data)
                }
            }
        }
    }

    private fun saveLoginData(res: LoginResponse?) {

        if (res != null) {
            val userData = User(
                id = res.data.first().id.toString(),
                name = res.data.first().name.toString(),
                email = res.data.first().email,
                phone_number = res.data.first().phone_number.toString(),
                location = res.data.first().location.toString(),
                roles = res.data.first().roles.toString(),
                token = res.token,
            )
            val userModel = DataMapper.mapUser(userData)
            val token = saveToLocal.getTokenFcm()
            session.setUser(userData)
            session.setLogin(true)
            viewModel.initUser(userModel, token)
            startToOTP(OtpArgs(userData, true))
        }
    }

    private fun startToOTP(value: OtpArgs) {
        if (value.user != null) {
            saveToLocal.setNumber(value.user!!.phone_number)
            val intent = Intent(this, SendOTPActivity::class.java)
            intent.putExtra(NUMBER, value)

            startActivity(intent)
        }
    }

    private val afterTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // ignore
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val emailInput: String = binding.edtEmail.text.toString().trim()
            val passwordInput: String = binding.edtPw.text.toString().trim()

            binding.btnLogin.isEnabled = (emailInput.isNotBlank()
                    && emailInput.isNotEmpty()
                    && passwordInput.isNotBlank()
                    && passwordInput.isNotEmpty())
        }

        override fun afterTextChanged(s: Editable) {
            // ignore
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_login -> {
                startActivity(Intent(this, EnterNumberActivity::class.java))
            }
            R.id.btn_login -> {
                sendRequestLogin()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}