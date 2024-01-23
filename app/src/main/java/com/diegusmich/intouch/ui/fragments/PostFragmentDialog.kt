package com.diegusmich.intouch.ui.fragments

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnScrollChangeListener
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.diegusmich.intouch.R
import com.diegusmich.intouch.authorization.Policies
import com.diegusmich.intouch.databinding.FragmentPostBinding
import com.diegusmich.intouch.providers.CloudImageProvider
import com.diegusmich.intouch.ui.activities.EventActivity
import com.diegusmich.intouch.ui.activities.UserActivity
import com.diegusmich.intouch.ui.adapters.PostImagesCarouselAdapter
import com.diegusmich.intouch.ui.viewmodels.PostViewModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.material.carousel.HeroCarouselStrategy
import org.ocpsoft.prettytime.PrettyTime

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

        if(viewModel.post.value == null){
            viewModel.onloadPost(postId, false)
        }

        binding.postSwipeRefreshLayout.setOnRefreshListener {
            viewModel.onloadPost(postId, true)
        }

        binding.postUserInfoCard.apply {
            usernameTextView.setTextColor(Color.WHITE)
            setOnClickListener {
                viewModel.post.value?.userInfo?.id?.let{
                    requireActivity().startActivity(Intent(requireContext(), UserActivity::class.java).apply {
                        putExtra(UserActivity.USER_ARG, it)
                    })
                }
            }
        }

        binding.postEventButton.setOnClickListener {
            viewModel.post.value?.let{
                requireContext().startActivity(Intent(requireContext(), EventActivity::class.java).apply {
                    putExtra(EventActivity.EVENT_ARG,it.eventInfo.id)
                })
            }
        }

        binding.deletePostButton.setOnClickListener {
            viewModel.onDeletePost()
        }

        binding.openCommentButton.setOnClickListener {
            requireActivity().supportFragmentManager.let{ fragmentManager ->
                viewModel.post.value?.id?.let {
                    val commentsBottomSheet = CommentsModalBottomFragment.newInstance(it)
                    fragmentManager.beginTransaction().apply {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        add(commentsBottomSheet, "PROFILE_IMAGE_FRAGMENT_DIALOG")
                        addToBackStack(null)
                        commit()
                    }
                }
            }
        }

        setupSliderCounter()
        observeData()
    }

    private fun observeData(){

        viewModel.post.observe(viewLifecycleOwner){
            it?.let {
                binding.carouselRecyclerView.apply {
                    val imagesRef = it.album.mapNotNull { imgName ->
                        CloudImageProvider.POSTS.imageRef(imgName)
                    }
                    adapter = PostImagesCarouselAdapter(imagesRef)
                }
                updateCarouselCounter(1)

                binding.postTopInfoGroup.visibility = View.VISIBLE
                binding.postBottomInfoGroup.visibility = View.VISIBLE

                binding.postEventButton.text = it.eventInfo.name
                binding.postDescriptionText.text = it.description
                binding.postUserInfoCard.apply {
                    setUserInfo(it.userInfo)
                }

                if(Policies.canDeletePost(it))
                    binding.deletePostButton.visibility = View.VISIBLE

                binding.postCreatedAtText.apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.post_created_at_formatted, PrettyTime().format(it.createdAt))
                }
            }
        }

        viewModel.POST_DELETED.observe(viewLifecycleOwner){
            if(it){
                dialog?.dismiss()
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

    private fun updateCarouselCounter(pos: Int){
        binding.postCarouselCounter.text = getString(R.string.post_carousel_counter, pos, viewModel.post.value?.album?.size)
    }

    private fun setupSliderCounter(){
        binding.carouselRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.carouselRecyclerView)

        binding.carouselRecyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val pos =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() + 1
                    updateCarouselCounter(pos)
                }
            }
        })
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