package com.diegusmich.intouch.ui.fragments

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentPostBinding
import com.diegusmich.intouch.providers.CloudImageProvider
import com.diegusmich.intouch.ui.adapters.PostImagesCarouselAdapter
import com.diegusmich.intouch.ui.viewmodels.PostViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.material.carousel.HeroCarouselStrategy

class PostFragmentDialog : DialogFragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private var postId : String? = null

    private val viewModel : PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getString(POST_ID_ARG)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(POST_ID_ARG, postId)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            postId = it.getString(POST_ID_ARG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme() = R.style.Intouch_Theme_FullScreenDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appBarLayout.materialToolbar.apply {
            setBackgroundColor(Color.BLACK)
            navigationIcon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_close_24)
            setNavigationIconTint(Color.WHITE)
            setNavigationOnClickListener {
                this@PostFragmentDialog.dismiss()
            }
        }

        if(viewModel.post.value == null){
            viewModel.onloadPost(postId, false)
        }

        binding.postSwipeRefreshLayout.setOnRefreshListener {
            viewModel.onloadPost(postId, true)
        }

        binding.carouselRecyclerView.layoutManager = CarouselLayoutManager(FullScreenCarouselStrategy())

        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(binding.carouselRecyclerView)

        observeData()
    }

    private fun observeData(){

        viewModel.post.observe(viewLifecycleOwner){
            it?.let {
                val imagesRef = it.album.mapNotNull { imgName ->
                    CloudImageProvider.POSTS.imageRef(imgName)
                }
                binding.carouselRecyclerView.adapter = PostImagesCarouselAdapter(imagesRef)
            }
        }

        viewModel.LOADING.observe(viewLifecycleOwner){
            binding.postSwipeRefreshLayout.isRefreshing = it
        }

        viewModel.ERROR.observe(viewLifecycleOwner){
            it?.let {
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val POST_ID_ARG = "postId"

        @JvmStatic
        fun newInstance(postId: String?) : PostFragmentDialog {
            return PostFragmentDialog().apply {
                arguments = bundleOf(POST_ID_ARG to postId)
            }
        }
    }
}