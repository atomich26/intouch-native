package com.diegusmich.intouch.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivityEditPreferencesBinding
import com.diegusmich.intouch.ui.viewmodels.EditPreferencesActivityViewModel
import com.diegusmich.intouch.ui.views.decorators.visible
import com.google.android.material.progressindicator.LinearProgressIndicator

class EditPreferencesActivity : AppCompatActivity() {

    private val viewModel: EditPreferencesActivityViewModel by viewModels()

    private var _binding: ActivityEditPreferencesBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressBar: LinearProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditPreferencesBinding.inflate(layoutInflater)

        intent?.extras?.let {
            viewModel.onSetEditMode(it.getBoolean(EDIT_MODE_ARG, false))
        }

        progressBar = binding.pgLayout.progressBar

        onBackPressedDispatcher.addCallback(this) {
            viewModel.editMode.value?.let {
                if(it && viewModel.LOADING.value == false)
                    this@EditPreferencesActivity.finish()
            }
        }

        viewModel.editMode.value?.let{
            if(it){
                binding.descriptionAddCategories.visibility = View.GONE
                binding.titleAddCategories.visibility = View.GONE
                binding.saveCategoriesButton.text = getString(R.string.action_save_changes_form)
                binding.skipCategoriesButton.visibility = View.INVISIBLE
                binding.appBarLayout.materialAppBarLayout.visibility = View.VISIBLE
                binding.appBarLayout.materialToolbar.apply {
                    title = getString(R.string.edit_preferences)
                    setNavigationOnClickListener { this@EditPreferencesActivity.onBackPressedDispatcher.onBackPressed() }
                    navigationIcon = AppCompatResources.getDrawable(this@EditPreferencesActivity, R.drawable.baseline_arrow_back_24)
                }
            }
            else{
                binding.saveCategoriesButton.text = getString(R.string.continue_text)
                binding.skipCategoriesButton.visibility = View.VISIBLE
                binding.descriptionAddCategories.visibility = View.VISIBLE
                binding.titleAddCategories.visibility = View.VISIBLE
                binding.appBarLayout.materialAppBarLayout.visibility = View.GONE
            }
        }

        binding.skipCategoriesButton.setOnClickListener {
            viewModel.onSaveCategories(true)
        }

        binding.filtersCategoryGroup.onCheckedChange {
            viewModel.onUpdateCheckedCategories(it.checkedFilters())
        }

        binding.saveCategoriesButton.setOnClickListener {
            viewModel.onSaveCategories()
        }

        setContentView(binding.root)
        observeData()
    }

    private fun observeData() {
        viewModel.PREFERENCES_SAVED.observe(this) {
            if (it) {
                if (viewModel.editMode.value!!)
                    this.onBackPressedDispatcher.onBackPressed()
                else
                    startMainActivity()
            }
        }

        viewModel.LOADING.observe(this) {
            progressBar.visible(it)
            binding.saveCategoriesButton.isEnabled = !it && viewModel.EDITED.value!!
            binding.skipCategoriesButton.isEnabled= !it
        }

        viewModel.ERROR.observe(this) {
            if (it != null)
                Toast.makeText(this@EditPreferencesActivity, getString(it), Toast.LENGTH_SHORT)
                    .show()
        }

        viewModel.EDITED.observe(this){
            binding.saveCategoriesButton.isEnabled = it && viewModel.LOADING.value == false
        }

        viewModel.checkedCategories.observe(this) {
            binding.saveCategoriesButton.isEnabled = it.isNotEmpty()
        }

        viewModel.categories.observe(this) { catList ->
            if (!catList.isNullOrEmpty())
                binding.filtersCategoryGroup.bindData(catList) { cat ->
                    viewModel.checkedCategories.value!!.any{ it == cat.id}
                }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this@EditPreferencesActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EDIT_MODE_ARG = "edit_mode"
    }
}