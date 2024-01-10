package com.diegusmich.intouch.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.diegusmich.intouch.databinding.ActivityStartBinding
import com.diegusmich.intouch.ui.viewmodels.StartActivityViewModel
import com.diegusmich.intouch.ui.views.decorators.visible
import com.google.android.material.progressindicator.LinearProgressIndicator

class StartActivity : AppCompatActivity() {

    private val viewModel : StartActivityViewModel by viewModels()

    private var _binding : ActivityStartBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressBar : LinearProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStartBinding.inflate(layoutInflater)

        progressBar = binding.pgLayout.progressBar

        onBackPressedDispatcher.addCallback(this){
            return@addCallback
        }

        binding.filtersCategoryGroup.onCheckedChange {
            viewModel.onUpdateCheckedCategories(it.checkedFilters())
        }

        binding.skipCategoriesButton.setOnClickListener {
            startMainActivity()
        }

        binding.saveCategoriesButton.setOnClickListener{
            viewModel.onSaveCategories()
        }

        setContentView(binding.root)
        observeData()
    }

    private fun observeData() {

        viewModel.PREFERENCES_SAVED.observe(this){
            if(it)
                startMainActivity()
        }

        viewModel.LOADING.observe(this){
            progressBar.visible(it)
            binding.saveCategoriesButton.isEnabled = !it && viewModel.checkedCategories.value!!.isNotEmpty()
        }

        viewModel.ERROR.observe(this){
            if(it != null)
                Toast.makeText(this@StartActivity, getString(it), Toast.LENGTH_SHORT).show()
        }

        viewModel.checkedCategories.observe(this){
            binding.saveCategoriesButton.isEnabled = it.isNotEmpty()
        }

        viewModel.categories.observe(this){
            if(!it.isNullOrEmpty())
                binding.filtersCategoryGroup.bindData(it){ cat ->
                    viewModel.checkedCategories.value!!.contains(cat.id)
                }
        }
    }

    private fun startMainActivity(){
        startActivity(Intent(this@StartActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}