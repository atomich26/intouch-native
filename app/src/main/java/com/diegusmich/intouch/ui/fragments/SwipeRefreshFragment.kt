package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.SwipeRefreshFragmentBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar

abstract class SwipeRefreshFragment : BaseFragment()  {

    private var _binding : SwipeRefreshFragmentBinding? = null
    private val binding get() = _binding!!

    private var _swipeRefreshLayout : SwipeRefreshLayout? = null
    protected val swipeRefreshLayout get() = _swipeRefreshLayout!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = SwipeRefreshFragmentBinding.inflate(inflater, container, false)
        _swipeRefreshLayout = binding.swipeRefreshFragmentLayout

        val rootView = inflateRootView(inflater, container)
        binding.swipeRefreshNested.addView(rootView)

        return binding.root
    }

    protected abstract fun inflateRootView(inflater : LayoutInflater, container: ViewGroup?) : ViewGroup
}