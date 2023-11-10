package com.diegusmich.intouch.ui.fragments.auth.signup

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
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
import com.diegusmich.intouch.databinding.FragmentSignupBinding
import com.diegusmich.intouch.ui.activities.start.StartActivity
import com.diegusmich.intouch.ui.fragments.BaseFragment
import com.diegusmich.intouch.ui.state.UiState
import com.diegusmich.intouch.ui.views.decorators.visible
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.util.Date

class SignupFragment : BaseFragment() {

    private var _binding : FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val viewModel : SignupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()

        binding.createAccountAppBar.title = getString(R.string.create_account_fragment_title)
        binding.createAccountAppBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        binding.createAccountAppBar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.textviewGdpr.text =
            Html.fromHtml(getString(R.string.text_gdpr_consent), Html.FROM_HTML_MODE_COMPACT)
        Linkify.addLinks(binding.textviewGdpr, Linkify.WEB_URLS)
        binding.textviewGdpr.movementMethod = LinkMovementMethod.getInstance()

        //Birthdate input
        val datePickerValidator = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.before(Date().time))

        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText(getString(R.string.date_picker_title))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setSelection(Date().time)
            .setCalendarConstraints(datePickerValidator.build())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            viewModel.onUpdateBirthdate(it)
        }

        datePicker.addOnDismissListener {
            binding.signupBirthdateTextField.clearFocus()
        }

        binding.signupBirthdateTextField.inputType = InputType.TYPE_NULL

        binding.signupBirthdateTextField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !datePicker.isVisible)
                datePicker.show(activity?.supportFragmentManager!!, null)
        }

        binding.signupEmailTextField.doAfterTextChanged {
            viewModel.onUpdateEmail(it.toString())
        }

        binding.signupPasswordTextField.doAfterTextChanged {
            viewModel.onUpdatePassword(it.toString())
        }

        binding.signupNameTextField.doAfterTextChanged {
            viewModel.onUpdateName(it.toString())
        }

        binding.signupUsernameTextField.doAfterTextChanged {
            viewModel.onUpdateUsername(it.toString())
        }

        binding.createUserNextButton.setOnClickListener {
            viewModel.createAccount()
        }

        startPostponedEnterTransition()
    }

    override fun lifecycleStateObserve() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){

                launch {
                    viewModel.name.collect{
                        binding.signupNameInputLayout.updateState(it)
                    }
                }

                launch {
                    viewModel.username.collect{
                        binding.signupUsernameInputLayout.updateState(it)
                    }
                }

                launch {
                    viewModel.email.collect{
                        binding.signupEmailInputLayout.updateState(it)
                    }
                }

                launch {
                    viewModel.password.collect{
                        binding.signupPasswordInputLayout.updateState(it)
                    }
                }

                launch {
                    viewModel.birthdate.collect{
                        binding.signupBirthdateInputLayout.updateState(it)
                    }
                }

                viewModel.uiState.collect{
                    when(it){
                        is UiState.LOADING -> {
                            binding.signupProgressBar.visible(true)
                            enableViews(false)
                        }

                        is UiState.LOADING_COMPLETED -> {
                            binding.signupProgressBar.visible(false)
                            enableViews(true)
                        }

                        is UiState.LOGGED -> {
                            requireActivity().startActivity(Intent(requireContext(), StartActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                            requireActivity().finish()
                        }

                        is UiState.ERROR -> {
                            Toast.makeText(requireContext(), getString(viewModel.errorMessage!!), Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                    viewModel.consumeEvent()
                }
            }
        }
    }

    private fun enableViews(enabled : Boolean) {
        binding.signupPasswordTextField.isEnabled = enabled
        binding.signupNameTextField.isEnabled = enabled
        binding.signupUsernameTextField.isEnabled = enabled
        binding.signupEmailTextField.isEnabled = enabled
        binding.createUserNextButton.isEnabled = enabled
        binding.signupBirthdateTextField.isEnabled = enabled
    }
}