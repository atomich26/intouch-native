package com.diegusmich.intouch.ui.activities

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivityUpsertEventBinding
import com.diegusmich.intouch.ui.viewmodels.UpsertEventViewModel
import com.diegusmich.intouch.ui.views.decorators.visible
import com.diegusmich.intouch.utils.FileUtil
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Date

class UpsertEventActivity : AppCompatActivity() {

    private var _binding: ActivityUpsertEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: MaterialToolbar
    private lateinit var datePickerBuilder : MaterialDatePicker.Builder<Long>
    private lateinit var timePickerBuilder : MaterialTimePicker.Builder

    private val viewModel: UpsertEventViewModel by viewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        it?.let {
            FileUtil.fileFromContentUri(this, it)?.let { tempFile ->
                viewModel.onUpdateCoverImage(this, tempFile)
            }
        }
    }

    private val locationPicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val location =
                    result.data?.getParcelableExtra(MapsPickerActivity.LOCATION_RESULT_KEY) as Location?
                location?.let {
                    viewModel.onUpdateLocation(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUpsertEventBinding.inflate(layoutInflater)

        val eventId = intent.extras?.getString(EVENT_ID_ARG)

        toolbar = binding.appBarLayout.materialToolbar
        toolbar.apply {
            title = if (eventId == null)
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
            binding.eventFormAvailableInputLayout.visibility =
                if (it == null) View.VISIBLE else View.GONE
        }

        val datePickerValidator = CalendarConstraints.Builder().setValidator(
            CompositeDateValidator.allOf(
                listOf(
                    DateValidatorPointForward.now(),
                )
            )
        )

        datePickerBuilder = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText(getString(R.string.date_picker_title))
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setCalendarConstraints(datePickerValidator.build())

        timePickerBuilder = MaterialTimePicker.Builder().apply{
            setTimeFormat(TimeFormat.CLOCK_24H)
            setTitleText(getString(R.string.time_picker_title))
        }


        binding.eventFormPickGeoButton.setOnClickListener {
            locationPicker.launch(Intent(this, MapsPickerActivity::class.java).apply {
                putExtra(MapsPickerActivity.LOCATION_ARG, viewModel.geo.value?.inputValue)
            })
        }

        binding.categoriesDropdown.setOnItemClickListener { _, _, pos, _ ->
            viewModel.onUpdateCategory(pos)
        }

        binding.eventFormAddImageButton.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.eventFormDescriptionInputLayout.editText?.doAfterTextChanged {
            it?.let {
                viewModel.onUpdateDescription(it.toString())
            }
        }

        binding.eventFormNameInputLayout.editText?.doAfterTextChanged {
            it?.let {
                viewModel.onUpdateName(it.toString())
            }
        }

        binding.eventFormCityInputLayout.editText?.doAfterTextChanged {
            it?.let {
                viewModel.onUpdateCity(it.toString())
            }
        }

        binding.eventFormAddressInputLayout.editText?.doAfterTextChanged {
            it?.let {
                viewModel.onUpdateAddress(it.toString())
            }
        }

        binding.eventFormAvailableInputLayout.editText?.doAfterTextChanged {
            it?.let {
                if (viewModel.editMode.value == false)
                    viewModel.onUpdateAvailable(it.toString())
            }
        }

        binding.eventFormSubmitButton.setOnClickListener {
            viewModel.onUpsertEvent()
        }

        binding.eventFormRestrictedSwitch.setOnCheckedChangeListener { _, value ->
            viewModel.onUpdateRestricted(value)
        }

        setupDateInputs()
        setContentView(binding.root)
        observeData()
    }

    private fun observeData() {

        viewModel.currentCoverImage.observe(this) {
            it?.let {
                binding.coverImageView.load(it)
                binding.eventFormAddImageButton.text = getString(R.string.prompt_event_edit_image)
            }
        }

        viewModel.coverFile.observe(this) {
            it?.let {
                binding.coverImageView.load(it)
                binding.eventFormAddImageButton.text = getString(R.string.prompt_event_edit_image)
            }
        }

        viewModel.name.observe(this) {
            binding.eventFormNameInputLayout.updateState(it)
        }

        viewModel.description.observe(this) {
            binding.eventFormDescriptionInputLayout.updateState(it)
        }

        viewModel.city.observe(this) {
            binding.eventFormCityInputLayout.updateState(it)
        }

        viewModel.address.observe(this) {
            binding.eventFormAddressInputLayout.updateState(it)
        }

        viewModel.category.observe(this) {
            binding.categoryFormEventInputLayout.updateState(it)
        }

        viewModel.available.observe(this) {
            binding.eventFormAvailableInputLayout.updateState(it)
        }

        viewModel.restricted.observe(this) {
            it.inputValue?.let { value ->
                binding.eventFormRestrictedSwitch.isChecked = value
            }
        }

        viewModel.startAt.observe(this) {
            it?.let {
                binding.eventFormStartAtInputLayout.updateState(it)
                binding.eventFormStartAtTimeInputLayout.updateState(it)
            }
        }

        viewModel.endAt.observe(this) {
            it?.let {
                binding.eventFormEndAtInputLayout.updateState(it)
                binding.eventFormEndAtTimeInputLayout.updateState(it)
            }
        }

        viewModel.categories.observe(this) {
            it?.let {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    it.map { cat -> cat.name }.toTypedArray()
                )
                (binding.categoryFormEventInputLayout.editText as? MaterialAutoCompleteTextView)?.setAdapter(
                    adapter
                )
            }
        }

        viewModel.EVENT_NOT_EXISTS.observe(this) {
            if (it)
                Toast.makeText(this, getString(R.string.event_not_exists), Toast.LENGTH_SHORT)
                    .show()
        }

        viewModel.LOCATION_ADDED.observe(this) {
            if (it)
                Toast.makeText(this, getString(R.string.geo_added_form_event), Toast.LENGTH_SHORT)
                    .show()
        }

        viewModel.CONTENT_LOADED.observe(this) {
            binding.eventFormParent.visibility = if (it) View.VISIBLE else View.INVISIBLE
            binding.eventFormButtonGroup.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }

        viewModel.EVENT_CREATED.observe(this) {
            if (it) {
                Toast.makeText(this, getString(R.string.event_created_message), Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, EventActivity::class.java).apply {
                    putExtra(EventActivity.EVENT_ARG, viewModel.newEventId)
                })
                finish()
            }
        }

        viewModel.EDITED.observe(this) {
            if (it) {
                Toast.makeText(this, getString(R.string.event_edited_message), Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }

        viewModel.LOADING.observe(this) {
            binding.pgLayout.progressBar.visible(it)
            binding.eventFormSubmitButton.isEnabled = !it
            binding.categoryFormEventInputLayout.isEnabled = !it
            binding.eventFormCityInputLayout.isEnabled = !it
            binding.eventFormAddressInputLayout.isEnabled = !it
            binding.eventFormRestrictedSwitch.isEnabled = !it
            binding.eventFormAddImageButton.isEnabled = !it
            binding.eventFormPickGeoButton.isEnabled = !it
            binding.eventFormAvailableInputLayout.isEnabled = !it
            binding.eventFormDescriptionInputLayout.isEnabled = !it
            binding.eventFormNameInputLayout.isEnabled = !it
            binding.eventFormStartAtInputLayout.isEnabled = !it
            binding.eventFormStartAtTimeInputLayout.isEnabled = !it
            binding.eventFormEndAtInputLayout.isEnabled = !it
            binding.eventFormEndAtTimeInputLayout.isEnabled = !it
        }

        viewModel.ERROR.observe(this) {
            it?.let {
                Toast.makeText(this, getString(it), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDateInputs() {

        binding.eventFormStartAtInputLayout.editText?.inputType = InputType.TYPE_NULL
        binding.eventFormStartAtInputLayout.editText?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                datePickerBuilder
                    .setSelection(viewModel.startAt.value?.inputValue?.time ?: Date().time)
                    .build().apply {
                        addOnPositiveButtonClickListener {
                            viewModel.onUpdateStartAt(it)
                        }
                        addOnDismissListener {
                            view.clearFocus()
                        }
                        show(supportFragmentManager, "DATEPICKER_START_AT")
                    }
            }
        }
        binding.eventFormEndAtInputLayout.editText?.inputType = InputType.TYPE_NULL
        binding.eventFormEndAtInputLayout.editText?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                datePickerBuilder
                    .setSelection(viewModel.endAt.value?.inputValue?.time ?: Date().time)
                    .build().apply {
                        addOnPositiveButtonClickListener {
                            viewModel.onUpdateEndAt(it)
                        }
                        addOnDismissListener {
                            view.clearFocus()
                        }
                        show(supportFragmentManager, "DATEPICKER_END_AT")
                    }
            }
        }

        binding.eventFormEndAtTimeInputLayout.editText?.inputType = InputType.TYPE_NULL
        binding.eventFormEndAtTimeInputLayout.editText?.setOnFocusChangeListener{ view, hasFocus ->
            if(hasFocus){
                viewModel.endAt.value.let {
                    timePickerBuilder
                        .setHour(it?.inputValue?.hours ?: 0)
                        .setMinute(it?.inputValue?.minutes ?: 0 )
                        .build().apply {
                        addOnPositiveButtonClickListener {
                            viewModel.onUpdateEndAt(minutes = this.minute, hours = this.hour)
                        }
                        addOnDismissListener {
                            view.clearFocus()
                        }
                        if(supportFragmentManager.findFragmentByTag("TIME_PICKER_END") == null)
                            show(supportFragmentManager, "TIME_PICKER_END")
                    }
                }
            }
        }

        binding.eventFormStartAtTimeInputLayout.editText?.inputType = InputType.TYPE_NULL
        binding.eventFormStartAtTimeInputLayout.editText?.setOnFocusChangeListener{ view, hasFocus ->
            if(hasFocus){
                viewModel.startAt.value.let{
                    timePickerBuilder
                        .setHour(it?.inputValue?.hours ?: 0)
                        .setMinute(it?.inputValue?.minutes ?: 0 )
                        .build().apply {
                        addOnPositiveButtonClickListener{
                            viewModel.onUpdateStartAt(minutes = this.minute, hours = this.hour)
                        }
                        addOnDismissListener {
                            view.clearFocus()
                        }
                        if(supportFragmentManager.findFragmentByTag("TIME_PICKER_START") == null)
                            show(supportFragmentManager, "TIME_PICKER_START")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EVENT_ID_ARG = "eventId"
    }
}