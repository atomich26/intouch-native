package com.diegusmich.intouch.ui.activities

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivityUpsertEventBinding
import com.diegusmich.intouch.databinding.MaterialToolbarBinding
import com.diegusmich.intouch.ui.viewmodels.UpsertEventViewModel
import com.diegusmich.intouch.ui.views.decorators.visible
import com.diegusmich.intouch.utils.FileUtil
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Date

class UpsertEventActivity : AppCompatActivity() {

    private var _binding : ActivityUpsertEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar : MaterialToolbar

   private lateinit var timePicker: MaterialTimePicker

    private val viewModel : UpsertEventViewModel by viewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        it?.let {
            FileUtil.fileFromContentUri(this, it)?.let { tempFile ->
                viewModel.onUpdateCoverImage( tempFile)
            }
        }
    }

    private val locationPicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val location = result.data?.getParcelableExtra(MapsPickerActivity.LOCATION_RESULT_KEY) as Location?
                location?.let{
                    viewModel.onUpdateLocation(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUpsertEventBinding.inflate(layoutInflater)

        val eventId = intent.extras?.getString(EVENT_ID_ARG)

        timePicker = MaterialTimePicker.Builder().run {
            setTimeFormat(TimeFormat.CLOCK_12H)
            setTitleText(getString(R.string.time_picker_title))
            build()
        }

        toolbar = binding.appBarLayout.materialToolbar
        toolbar.apply {
            title = if(eventId == null)
                getString(R.string.create_event_title)
            else
                getString(R.string.edit_event_title)
            setNavigationIcon(R.drawable.baseline_arrow_back_24)
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        intent.extras?.getString(EVENT_ID_ARG).let {
            viewModel.setEditMode(it)
            binding.eventFormAvailableInputLayout.visibility = if(it == null) View.VISIBLE else View.GONE
        }

        binding.eventFormPickGeoButton.setOnClickListener {
            locationPicker.launch(Intent(this, MapsPickerActivity::class.java).apply {
                putExtra(MapsPickerActivity.LOCATION_ARG, viewModel.geo.value?.inputValue)
            })
        }

        binding.categoriesDropdown.setOnItemClickListener{_, _, pos, _ ->
            viewModel.onUpdateCategory(pos)
        }

        binding.eventFormAddImageButton.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.eventFormEndAtTimeTextField.setOnClickListener {
            timePicker.addOnPositiveButtonClickListener{

            }
            showTimePicker()
        }

        binding.eventFormDescriptionInputLayout.editText?.doAfterTextChanged {
            it?.let{
                viewModel.onUpdateDescription(it.toString())
            }
        }

        binding.eventFormNameInputLayout.editText?.doAfterTextChanged {
            it?.let{
                viewModel.onUpdateName(it.toString())
            }
        }

        binding.eventFormCityInputLayout.editText?.doAfterTextChanged {
            it?.let {
                viewModel.onUpdateCity(it.toString())
            }
        }

        binding.eventFormAddressInputLayout.editText?.doAfterTextChanged {
            it?.let{
                viewModel.onUpdateAddress(it.toString())
            }
        }

        binding.eventFormAvailableInputLayout.editText?.doAfterTextChanged {
            it?.let {
                if(viewModel.editMode.value == false)
                    viewModel.onUpdateAvailable(it.toString())
            }
        }

        binding.eventFormSubmitButton.setOnClickListener {
            viewModel.onUpsertEvent()
        }

        binding.eventFormEndAtTimeTextField.setOnClickListener {
            timePicker.addOnPositiveButtonClickListener{

            }
            showTimePicker()
        }


        binding.eventFormRestrictedSwitch.setOnCheckedChangeListener{ _, value ->
            viewModel.onUpdateRestricted(value)
        }

        setContentView(binding.root)
        observeData()
    }

    private fun observeData(){
        viewModel.cover.observe(this){
            it?.let{
                it.inputValue?.let { file ->
                    binding.coverImageView.load(file)
                    binding.eventFormAddImageButton.text = getString(R.string.prompt_event_edit_image)
                }
            }
        }

        viewModel.name.observe(this){
            binding.eventFormNameInputLayout.updateState(it)
        }

        viewModel.description.observe(this){
            binding.eventFormDescriptionInputLayout.updateState(it)
        }

        viewModel.city.observe(this){
            binding.eventFormCityInputLayout.updateState(it)
        }

        viewModel.address.observe(this){
            binding.eventFormAddressInputLayout.updateState(it)
        }

        viewModel.category.observe(this){
            binding.categoryFormEventInputLayout.updateState(it)
        }

        viewModel.restricted.observe(this){
            it.inputValue?.let{ value ->
                binding.eventFormRestrictedSwitch.isChecked = value
            }
        }

        viewModel.categories.observe(this){
            it?.let{
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, it.map { cat -> cat.name }.toTypedArray())
                (binding.categoryFormEventInputLayout.editText as? MaterialAutoCompleteTextView)?.setAdapter(adapter)
            }
        }

        viewModel.EVENT_NOT_EXISTS.observe(this){
            if(it)
                Toast.makeText(this, getString(R.string.event_not_exists), Toast.LENGTH_SHORT).show()
        }

        viewModel.LOCATION_ADDED.observe(this){
            if(it)
                Toast.makeText(this, getString(R.string.geo_added_form_event), Toast.LENGTH_SHORT).show()
        }

        viewModel.CONTENT_LOADED.observe(this){
            binding.eventFormParent.visibility = if(it) View.VISIBLE else View.INVISIBLE
            binding.eventFormButtonGroup.visibility = if(it) View.VISIBLE else View.INVISIBLE
        }

        viewModel.LOADING.observe(this){
            binding.pgLayout.progressBar.visible(it)
        }

        viewModel.ERROR.observe(this){
            it?.let{
                Toast.makeText(this, getString(it), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDateInput() {
        val datePickerValidator = CalendarConstraints.Builder().setValidator(
            CompositeDateValidator.allOf(
                listOf(
                    DateValidatorPointForward.now(),
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

        /*datePicker.addOnPositiveButtonClickListener {
            viewModel.onUpdateBirthdate(it)
        }

        datePicker.addOnDismissListener {
            binding.form.userFormBirthdateTextField.clearFocus()
        }

        binding.form.userFormBirthdateTextField.inputType = InputType.TYPE_NULL

        binding.eventFormStartAtTextField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !datePicker.isVisible)
                datePicker.show(activity?.supportFragmentManager!!, null)
        }*/
    }

    private fun showTimePicker(){
        timePicker.show(supportFragmentManager, "TIME_PICKER")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val EVENT_ID_ARG = "eventId"
    }
}