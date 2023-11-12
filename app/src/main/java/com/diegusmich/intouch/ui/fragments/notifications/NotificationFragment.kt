package com.diegusmich.intouch.ui.fragments.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentNotificationBinding
import com.diegusmich.intouch.ui.fragments.SwipeRefreshFragment

class NotificationFragment :SwipeRefreshFragment() {

    private var _binding : FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel : NotificationViewModel by viewModels()

    override fun inflateRootView(inflater: LayoutInflater, container: ViewGroup?): ViewGroup {
       _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.notifications_title)
    }

    override fun lifecycleStateObserve() {
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}