package com.diegusmich.intouch.ui.fragments

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
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentSignupBinding
import com.diegusmich.intouch.ui.activities.StartActivity
import com.diegusmich.intouch.ui.viewmodels.SignupViewModel
import com.diegusmich.intouch.ui.views.decorators.visible
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.util.Date

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var toolbar: MaterialToolbar

    private val viewModel: SignupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(layoutInflater)

        progressBar = binding.pgLayout.progressBar
        toolbar = binding.appBarLayout.materialToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        toolbar.title = getString(R.string.create_account_fragment_title)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        with(binding.textviewGdpr) {
            text = Html.fromHtml(getString(R.string.text_gdpr_consent), Html.FROM_HTML_MODE_COMPACT)
            Linkify.addLinks(binding.textviewGdpr, Linkify.WEB_URLS)
            movementMethod = LinkMovementMethod.getInstance()
        }

        setupBirthdateInput()

        binding.signupEmailTextField.doAfterTextChanged {
            viewModel.onUpdateEmail(it.toString())
        }

        binding.signupPasswordTextField.doOnTextChanged { text, start, before, count ->
            if (count != before)
                viewModel.onUpdatePassword(text.toString())
        }

        binding.signupNameTextField.doAfterTextChanged {
            viewModel.onUpdateName(it.toString())
        }

        binding.signupUsernameTextField.doAfterTextChanged {
            viewModel.onUpdateUsername(it.toString())
        }

        binding.createUserNextButton.setOnClickListener {
            viewModel.onCreateAccount()
        }

        startPostponedEnterTransition()
        observeData()
    }

    private fun observeData() {
        viewModel.name.observe(viewLifecycleOwner) {
            binding.signupNameInputLayout.updateState(it)
        }

        viewModel.username.observe(viewLifecycleOwner) {
            binding.signupUsernameInputLayout.updateState(it)
        }

        viewModel.email.observe(viewLifecycleOwner) {
            binding.signupEmailInputLayout.updateState(it)
        }

        viewModel.password.observe(viewLifecycleOwner) {
            binding.signupPasswordInputLayout.updateState(it)
        }

        viewModel.birthdate.observe(viewLifecycleOwner) {
            binding.signupBirthdateInputLayout.updateState(it)
        }

        viewModel.ERROR.observe(viewLifecycleOwner) {
            if (it != null)
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT).show()
        }

        viewModel.LOADING.observe(viewLifecycleOwner) {
            progressBar.visible(it)
            binding.signupPasswordTextField.isEnabled = !it
            binding.signupNameTextField.isEnabled = !it
            binding.signupUsernameTextField.isEnabled = !it
            binding.signupEmailTextField.isEnabled = !it
            binding.createUserNextButton.isEnabled = !it
            binding.signupBirthdateTextField.isEnabled = !it
        }

        viewModel.LOGGED.observe(viewLifecycleOwner) {
            if (it) {
                requireActivity().startActivity(
                    Intent(
                        requireContext(),
                        StartActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                requireActivity().finish()
            }
        }
    }

    private fun setupBirthdateInput() {
        val datePickerValidator = CalendarConstraints.Builder().setValidator(
            CompositeDateValidator.allOf(
                listOf(
                    DateValidatorPointBackward.now(),
                    DateValidatorPointForward.from(-2208988800000)
                )
            )
        )

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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}