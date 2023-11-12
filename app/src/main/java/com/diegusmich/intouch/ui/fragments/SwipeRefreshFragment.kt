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

    private var _toolbar: MaterialToolbar? = null
    protected val toolbar get() = _toolbar!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = SwipeRefreshFragmentBinding.inflate(inflater, container, false)
        _swipeRefreshLayout = binding.swipeRefreshFragmentLayout

        val appBarLayout = onCreateToolbar(inflater, container)
        if(appBarLayout != null){
            _toolbar = appBarLayout.findViewById(R.id.materialToolbar)
            (binding.root as ViewGroup).addView(appBarLayout, 0)
        }

        val rootView = inflateRootView(inflater, container)
        binding.swipeRefreshNested.addView(rootView)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setColorSchemeResources(R.color.seed)
        swipeRefreshLayout.setProgressViewOffset(true, 0, 120)
    }

    protected open fun onCreateToolbar(inflater: LayoutInflater, container : ViewGroup?) : AppBarLayout?{
        return inflater.inflate(R.layout.material_toolbar, container, false) as AppBarLayout
    }

    protected abstract fun inflateRootView(inflater : LayoutInflater, container: ViewGroup?) : ViewGroup
}