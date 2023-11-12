package com.diegusmich.intouch.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentProfileBinding
import com.diegusmich.intouch.ui.fragments.SwipeRefreshFragment

class ProfileFragment : SwipeRefreshFragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel : ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = getString(R.string.profile_title)

        toolbar.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_auth_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                TODO("Not yet implemented")
            }

        }, viewLifecycleOwner)
    }

    override fun inflateRootView(inflater: LayoutInflater, container: ViewGroup?): ViewGroup {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun lifecycleStateObserve() {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}