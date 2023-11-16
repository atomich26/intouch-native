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
import com.diegusmich.intouch.ui.activities.BaseActivity
import com.diegusmich.intouch.ui.activities.main.MainActivity
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
            if(it != null)
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