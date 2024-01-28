package com.diegusmich.intouch.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivityUpsertEventBinding
import com.diegusmich.intouch.databinding.MaterialToolbarBinding
import com.diegusmich.intouch.ui.viewmodels.UpsertEventViewModel
import com.diegusmich.intouch.utils.FileUtil
import com.google.android.material.appbar.MaterialToolbar

class UpsertEventActivity : AppCompatActivity() {

    private var _binding : ActivityUpsertEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar : MaterialToolbar

    private val viewModel : UpsertEventViewModel by viewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        it?.let {
            FileUtil.fileFromContentUri(this, it)?.let { tempFile ->
                //viewModel.onLoadImage(this, tempFile)
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
            startActivity(Intent(this, MapsPickerActivity::class.java))
        }
        setContentView(binding.root)
    }

    private fun observeData(){

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val EVENT_ID_ARG = "eventId"
    }
}