package com.diegusmich.intouch.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentLoginBinding
import com.diegusmich.intouch.ui.activities.MainActivity
import com.diegusmich.intouch.ui.viewmodels.LoginViewModel
import com.diegusmich.intouch.ui.views.decorators.visible
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.LinearProgressIndicator

class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressBar : LinearProgressIndicator
    private lateinit var toolbar : MaterialToolbar

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        progressBar = binding.pgLayout.progressBar
        toolbar = binding.appBarLayout.materialToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()

        super.onViewCreated(view, savedInstanceState)

        toolbar.title = getString(R.string.login_fragment_title)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.loginEmailTextField.doAfterTextChanged {
            viewModel.onUpdateEmail(it.toString())
        }

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

        viewModel.email.observe(viewLifecycleOwner){
             binding.loginEmailInputLayout.updateState(it)
        }

        viewModel.password.observe(viewLifecycleOwner){
            binding.loginPasswordInputLayout.updateState(it)
        }

        viewModel.RECOVERY_EMAIL_SENT.observe(viewLifecycleOwner){
            if(it){
                Toast.makeText(
                    requireContext(),
                    R.string.recover_email_sent,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.LOADING.observe(viewLifecycleOwner){
            progressBar.visible(it)
            binding.loginEmailInputLayout.isEnabled = !it
            binding.loginPasswordInputLayout.isEnabled = !it
            binding.loginButton.isEnabled = !it
            binding.recoverPasswordButton.isEnabled = !it
        }

        viewModel.LOGGED.observe(viewLifecycleOwner){
            if(it){
                startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                requireActivity().finish()
            }
        }

        viewModel.ERROR.observe(viewLifecycleOwner){
            if(it != null){
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT).show()
            }
        }
    }
}