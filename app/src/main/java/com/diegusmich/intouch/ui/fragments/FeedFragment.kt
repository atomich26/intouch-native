package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.databinding.FragmentFeedBinding
import com.diegusmich.intouch.ui.adapters.PostFeedAdapter
import com.diegusmich.intouch.ui.viewmodels.FeedViewModel
import com.google.android.material.appbar.MaterialToolbar

class FeedFragment : Fragment() {

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

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onLoadMainFeed(true)
        }

        binding.postsFeedListView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.postsFeedListView.adapter = PostFeedAdapter(mutableListOf())

        viewModel.onLoadMainFeed()
        observeData()
    }

    private fun observeData(){
        viewModel.LOADING.observe(viewLifecycleOwner){
            binding.swipeRefreshLayout.isRefreshing = it
        }

        viewModel.postFeed.observe(viewLifecycleOwner){
            it?.let {
                binding.postsFeedListView.apply {
                    visibility = if(it.isEmpty()) View.GONE else View.VISIBLE
                    (adapter as PostFeedAdapter).replace(it)
                }
            }
        }

        viewModel.ERROR.observe(viewLifecycleOwner){
            it?.let {
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT).show()
            }
        }
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