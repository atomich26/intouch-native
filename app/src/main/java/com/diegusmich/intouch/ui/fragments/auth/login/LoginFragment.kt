package com.diegusmich.intouch.ui.fragments.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentLoginBinding
import com.diegusmich.intouch.ui.activities.MainActivity
import com.diegusmich.intouch.ui.fragments.BaseFragment
import com.diegusmich.intouch.ui.state.UiState
import com.diegusmich.intouch.ui.views.decorators.visible
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()

        super.onViewCreated(view, savedInstanceState)

        binding.loginAppBar.title = getString(R.string.login_fragment_title)
        binding.loginAppBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        binding.loginAppBar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.loginEmailInputLayout.updateState(viewModel.email)
        binding.loginEmailTextField.doAfterTextChanged {
            viewModel.onUpdateEmail(it.toString())
        }

        binding.loginPasswordInputLayout.updateState(viewModel.password)
        binding.loginPasswordTextField.doAfterTextChanged {
            viewModel.onUpdatePassword(it.toString())
        }

        binding.recoverPasswordButton.setOnClickListener {
            viewModel.onSendResetPasswordEmail()
        }

        binding.loginButton.setOnClickListener {
            viewModel.onPerformLogin()
        }

        startPostponedEnterTransition()
    }

    override fun lifecycleStateObserve() {

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when (it) {
                        is UiState.LOADING ->{
                            binding.loginProgressBar.visible(true)
                            enableViews(false)
                        }

                        is UiState.LOADING_COMPLETED -> {
                            binding.loginProgressBar.visible(false)
                            enableViews(true)
                        }

                        is UiState.LOGGED -> {
                            activity?.finishAndRemoveTask()
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                        }

                        is UiState.RECOVERY_EMAIL_SENT -> {
                            Toast.makeText(
                                requireContext(),
                                R.string.recover_email_sent,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is UiState.ERROR -> {
                            Toast.makeText(
                                requireContext(),
                                getString(viewModel.errorMessage!!),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> Unit
                    }
                    viewModel.consumeEvent()
                }
            }
        }
    }

    private fun enableViews(enabled : Boolean){
        binding.loginEmailTextField.isEnabled = enabled
        binding.loginPasswordInputLayout.isEnabled = enabled
        binding.loginButton.isEnabled = enabled
        binding.recoverPasswordButton.isEnabled = enabled
    }
}