package com.diegusmich.intouch.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivityAddPostBinding
import com.diegusmich.intouch.ui.adapters.AddPostCarouselAdapter
import com.diegusmich.intouch.ui.adapters.PostImagesCarouselAdapter
import com.diegusmich.intouch.ui.viewmodels.AddPostViewModel
import com.diegusmich.intouch.ui.views.decorators.visible
import com.diegusmich.intouch.utils.FileUtil
import com.google.android.material.appbar.MaterialToolbar

class AddPostActivity : AppCompatActivity() {

    private var _binding : ActivityAddPostBinding? = null
    val binding get() = _binding!!

    private lateinit var toolbar : MaterialToolbar

    private val viewModel : AddPostViewModel by viewModels()

    private val pickImages = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(4)) {
        it?.mapNotNull { uri ->
            FileUtil.fileFromContentUri(this, uri)
        }?.let { listFile ->
            if(listFile.isNotEmpty() )
                viewModel.onLoadImages(listFile)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.extras?.getString(EVENT_ID)?.let {
            viewModel.setEventId(it)
        }

        toolbar = binding.appBarLayout.materialToolbar
        toolbar.apply {
            navigationIcon = AppCompatResources.getDrawable(this@AddPostActivity, R.drawable.baseline_arrow_back_24)
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            title = getString(R.string.add_post_title)
        }

        onBackPressedDispatcher.addCallback {
            if(viewModel.LOADING.value == false){
                finish()
            }
        }

        binding.descriptionPostFormInputLayout.editText?.doAfterTextChanged {
            it?.let{
                viewModel.onUpdateDescription(it.toString())
            }
        }

        binding.submitFormPost.setOnClickListener {
            viewModel.onAddPost(this)
        }

        binding.selectImagesPicker.setOnClickListener {
            pickImages.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        setupSlider()
        observeData()
    }


    private fun observeData(){
        viewModel.LOADING.observe(this){
            binding.pgLayout.progressBar.visible(it)
            binding.submitFormPost.isEnabled = !it && viewModel.images.value!!.isNotEmpty()
            binding.descriptionPostFormInputLayout.isEnabled = !it
            binding.selectImagesPicker.isEnabled = !it
        }

        viewModel.images.observe(this){
            binding.submitFormPost.isEnabled = it.isNotEmpty()

            if(it.isNotEmpty()) {
                binding.submitFormPost.isEnabled = true
                binding.imageCounter.visibility = View.VISIBLE
                (binding.postImagesRecyclerView.adapter as AddPostCarouselAdapter).replace(it)
                updateCarouselCounter(1)
            }
            else
                binding.imageCounter.visibility = View.INVISIBLE
        }

        viewModel.POST_ADDED.observe(this){
            if(it){
                Toast.makeText(this, getString(R.string.post_added), Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.ERROR.observe(this){
            it?.let{
                Toast.makeText(this, getString(it), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCarouselCounter(pos: Int) {
        viewModel.images.value?.let{
            binding.imageCounter.text =
                getString(R.string.post_carousel_counter, pos, it.size)
        }
    }

    private fun setupSlider() {
        binding.postImagesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.postImagesRecyclerView.adapter = AddPostCarouselAdapter(mutableListOf())
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.postImagesRecyclerView)

        binding.postImagesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val pos =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() + 1
                    updateCarouselCounter(pos)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val EVENT_ID = "eventId"
    }
}