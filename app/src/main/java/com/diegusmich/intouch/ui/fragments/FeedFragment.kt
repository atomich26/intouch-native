package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.databinding.FragmentFeedBinding
import com.diegusmich.intouch.ui.adapters.PostFeedAdapter
import com.diegusmich.intouch.ui.viewmodels.FeedViewModel
import com.google.android.material.appbar.MaterialToolbar

class FeedFragment : BaseFragment() {

    private var _binding : FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar : MaterialToolbar

    private val viewModel : FeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentFeedBinding.inflate(layoutInflater, container, false)
        toolbar = binding.appBarLayout.materialToolbar

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.feed_title)

        binding.postsFeedListView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val fakeData = mutableListOf<Post.FeedPreview>()

        for (i in 0..10){
            fakeData.add(Post.FeedPreview("sada", false,
                User.Preview("sadas", false,  "sdasda", "adasdasd", "sadasd")))
        }
        binding.postsFeedListView.adapter = PostFeedAdapter(fakeData)

        viewModel.load()
    }

    override fun lifecycleStateObserve() {

    }

    override fun onResume() {
        super.onResume()
        binding.swipeRefreshLayout.onResumeView(viewModel.LOADING.value!!)
    }

    override fun onPause() {
        super.onPause()
        binding.swipeRefreshLayout.onPauseView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}