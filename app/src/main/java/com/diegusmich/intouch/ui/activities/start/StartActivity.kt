package com.diegusmich.intouch.ui.activities.start

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.diegusmich.intouch.databinding.ActivityStartBinding
import com.diegusmich.intouch.databinding.LinearProgressBarBinding
import com.diegusmich.intouch.ui.activities.BaseActivity
import com.diegusmich.intouch.ui.activities.main.MainActivity
import com.diegusmich.intouch.ui.state.UiState
import com.diegusmich.intouch.ui.views.decorators.visible
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.launch

class StartActivity : BaseActivity() {

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
            viewModel.saveCategories()
        }

        setContentView(binding.root)
    }

    override fun lifecycleStateObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                launch {
                    viewModel.checkedCategories.collect{
                        binding.saveCategoriesButton.isEnabled = it.isNotEmpty()
                    }
                }

                launch {
                    viewModel.categories.collect{
                        if(it != null)
                            binding.filtersCategoryGroup.bindData(it){cat ->
                                viewModel.checkedCategories.value.contains(cat.id)
                            }
                    }
                }

                viewModel.uiState.collect{
                    when(it){
                        is UiState.LOADING -> {
                            progressBar.visible(true)
                            binding.saveCategoriesButton.isEnabled = false
                        }
                        is UiState.LOADING_COMPLETED -> {
                            progressBar.visible(false)
                            binding.saveCategoriesButton.isEnabled = viewModel.checkedCategories.value.isNotEmpty()
                        }
                        is UiState.ERROR -> {
                            Toast.makeText(this@StartActivity, getString(viewModel.errorMessage!!), Toast.LENGTH_SHORT).show()
                        }
                        is UiState.PREFERENCES_SAVED -> {
                            startMainActivity()
                        }
                        else -> Unit
                    }
                    viewModel.consumeEvent()
                }
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