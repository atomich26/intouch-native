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
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentSignupBinding
import com.diegusmich.intouch.ui.activities.EditUserActivity
import com.diegusmich.intouch.ui.activities.EditPreferencesActivity
import com.diegusmich.intouch.ui.viewmodels.UpsertUserViewModel
import com.diegusmich.intouch.ui.views.decorators.visible
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.util.Date

class UpsertUserFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var toolbar: MaterialToolbar

    private val viewModel: UpsertUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.onSetEditMode(it.getBoolean(EditUserActivity.EDIT_MODE_ARG, false))
        }
    }

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

        if (viewModel.editMode.value!!){
            toolbar.title = getString(R.string.create_account_fragment_title)
            binding.form.root.visibility = View.INVISIBLE
            viewModel.onLoadUserCurrentData()

        }
        else
            toolbar.title = getString(R.string.user_edit_title)

        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        setupBirthdateInput()

        with(binding.form.textviewGdpr) {
            text = Html.fromHtml(getString(R.string.text_gdpr_consent), Html.FROM_HTML_MODE_COMPACT)
            Linkify.addLinks(binding.form.textviewGdpr, Linkify.WEB_URLS)
            movementMethod = LinkMovementMethod.getInstance()
        }

        //Sto schifo di Views non vanno bene per l'architettura MVVM
        binding.form.userFormEmailTextField.doAfterTextChanged {
            it?.let {
                viewModel.onUpdateEmail(it.toString())
            }
        }

        binding.form.userFormPasswordTextField.doOnTextChanged { text, start, before, count ->
            if (count != before)
                viewModel.onUpdatePassword(text.toString())
        }

        binding.form.userFormBiographyTextField.doAfterTextChanged {
            it?.let {
                viewModel.onUpdateBiography(it.toString())
            }
        }

        binding.form.userFormNameTextField.doAfterTextChanged {
            it?.let {
                viewModel.onUpdateName(it.toString())
            }
        }

        binding.form.userFormUsernameTextField.doAfterTextChanged {
            it?.let{
                viewModel.onUpdateUsername(it.toString())
            }
        }

        binding.form.userFormSubmitButton.setOnClickListener {
            viewModel.onUpsertProfile()
        }

        binding.form.userFormDistanceSlider.addOnChangeListener { slider, value, fromUser ->
            viewModel.onUpdateDistanceRange(value)
        }

        binding.form.userFormResetPasswordButton.setOnClickListener {
            viewModel.onSendResetPasswordEmail()
        }

        startPostponedEnterTransition()
        observeData()
    }

    private fun observeData() {
        viewModel.name.observe(viewLifecycleOwner) {
            binding.form.userFormNameInputLayout.updateState(it)
        }

        viewModel.username.observe(viewLifecycleOwner) {
            binding.form.userFormUsernameInputLayout.updateState(it)
        }

        viewModel.email.observe(viewLifecycleOwner) {
            if(!viewModel.editMode.value!!)
                binding.form.userFormEmailInputLayout.updateState(it)
        }

        viewModel.biography.observe(viewLifecycleOwner){
            if(viewModel.editMode.value!!)
                binding.form.userFormBiographyInputLayout.updateState(it)
        }

        viewModel.password.observe(viewLifecycleOwner) {
            if(!viewModel.editMode.value!!)
                binding.form.userFormPasswordInputLayout.updateState(it)
        }

        viewModel.birthdate.observe(viewLifecycleOwner) {
            binding.form.userFormBirthdateInputLayout.updateState(it)
        }

        viewModel.ERROR.observe(viewLifecycleOwner) {
            if (it != null)
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT).show()
        }

        viewModel.distance.observe(viewLifecycleOwner) {
            it?.inputValue?.let { value ->
                binding.form.userFormDistanceSlider.value = value.toFloat()
                binding.form.userFormSliderDistanceText.text =
                    getString(R.string.distance_formatted, value)
            }
        }

        viewModel.CONTENT_LOADED.observe(viewLifecycleOwner){
            if(it)
                binding.form.root.visibility = View.VISIBLE
        }

        viewModel.editMode.observe(viewLifecycleOwner) {
            if (it) {
                toolbar.title = getString(R.string.user_edit_title)
                binding.form.userFormSubmitButton.text =
                    getString(R.string.action_save_changes_form)
                binding.form.userFormEmailInputLayout.visibility = View.GONE
                binding.form.userFormDistanceSliderGroup.visibility = View.VISIBLE
                binding.form.userFormResetPasswordButton.visibility = View.VISIBLE
                binding.form.userFormBiographyInputLayout.visibility = View.VISIBLE
                binding.form.textviewGdpr.visibility = View.GONE
                binding.form.userFormPasswordInputLayout.visibility = View.GONE
            } else {
                toolbar.title = getString(R.string.create_account_fragment_title)
                binding.form.userFormSubmitButton.text =
                    getString(R.string.action_create_account_short)
                binding.form.userFormEmailInputLayout.visibility = View.VISIBLE
                binding.form.userFormDistanceSliderGroup.visibility = View.GONE
                binding.form.userFormResetPasswordButton.visibility = View.INVISIBLE
                binding.form.userFormBiographyInputLayout.visibility = View.GONE
                binding.form.textviewGdpr.visibility = View.VISIBLE
                binding.form.userFormPasswordInputLayout.visibility = View.VISIBLE
            }
        }

        viewModel.LOADING.observe(viewLifecycleOwner) {
            progressBar.visible(it)
            binding.form.userFormPasswordTextField.isEnabled = !it
            binding.form.userFormNameTextField.isEnabled = !it
            binding.form.userFormUsernameTextField.isEnabled = !it
            binding.form.userFormEmailTextField.isEnabled = !it
            binding.form.userFormSubmitButton.isEnabled = !it
            binding.form.userFormBirthdateTextField.isEnabled = !it
            binding.form.userFormDistanceSlider.isEnabled = !it
            binding.form.userFormBiographyTextField.isEnabled = !it
            binding.form.userFormResetPasswordButton.isEnabled = !it
        }

        viewModel.EDITED.observe(viewLifecycleOwner) {
            if (it){
                Toast.makeText(
                    requireContext(),
                    getString(R.string.user_edited_success),
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        viewModel.RECOVERY_EMAIL_SENT.observe(viewLifecycleOwner) {
            if (it)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.recover_email_sent),
                    Toast.LENGTH_SHORT
                ).show()
        }

        viewModel.LOGGED.observe(viewLifecycleOwner) {
            if (it) {
                requireActivity().startActivity(
                    Intent(
                        requireContext(),
                        EditPreferencesActivity::class.java
                    ).apply {
                        putExtra(EditPreferencesActivity.EDIT_MODE_ARG, false)
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
                    DateValidatorPointForward.from(-2208988800000),
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
            binding.form.userFormBirthdateTextField.clearFocus()
        }

        binding.form.userFormBirthdateTextField.inputType = InputType.TYPE_NULL

        binding.form.userFormBirthdateTextField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !datePicker.isVisible)
                datePicker.show(activity?.supportFragmentManager!!, null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(editMode: Boolean): UpsertUserFragment {
            return UpsertUserFragment().apply {
                arguments = bundleOf(EditUserActivity.EDIT_MODE_ARG to editMode)
            }
        }
    }
}