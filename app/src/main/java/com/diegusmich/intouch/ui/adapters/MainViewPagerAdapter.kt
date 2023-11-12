package com.diegusmich.intouch.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainViewPagerAdapter(
    private val destinations: Array<Fragment>,
    fragmentHost : FragmentManager,
    hostLifecycle: Lifecycle
) : FragmentStateAdapter(fragmentHost, hostLifecycle) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return destinations[position]
    }
}