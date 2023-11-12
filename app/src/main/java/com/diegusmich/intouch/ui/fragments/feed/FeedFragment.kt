package com.diegusmich.intouch.ui.fragments.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentFeedBinding
import com.diegusmich.intouch.ui.fragments.SwipeRefreshFragment

class FeedFragment : SwipeRefreshFragment() {

    private var _binding : FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel : FeedViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.feed_title)
    }

    override fun inflateRootView(inflater: LayoutInflater, container: ViewGroup?): ViewGroup {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun lifecycleStateObserve() {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}