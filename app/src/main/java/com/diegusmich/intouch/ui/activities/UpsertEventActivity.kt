package com.diegusmich.intouch.ui.activities

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivityUpsertEventBinding
import com.diegusmich.intouch.databinding.MaterialToolbarBinding
import com.diegusmich.intouch.ui.viewmodels.UpsertEventViewModel
import com.diegusmich.intouch.ui.views.decorators.visible
import com.diegusmich.intouch.utils.FileUtil
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.appbar.MaterialToolbar

class UpsertEventActivity : AppCompatActivity() {

    private var _binding : ActivityUpsertEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar : MaterialToolbar

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

        binding.eventFormPickGeoButton.setOnClickListener {
            locationPicker.launch(Intent(this, MapsPickerActivity::class.java).apply {
                putExtra(MapsPickerActivity.LOCATION_ARG, viewModel.geo.value?.inputValue)
            })
        }

        binding.eventFormAddImageButton.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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

        viewModel.EVENT_NOT_EXISTS.observe(this){
            if(it)
                Toast.makeText(this, getString(R.string.event_not_exists), Toast.LENGTH_SHORT).show()
        }

        viewModel.LOCATION_ADDED.observe(this){
            if(it)
                Toast.makeText(this, getString(R.string.geo_added_form_event), Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val EVENT_ID_ARG = "eventId"
    }
}