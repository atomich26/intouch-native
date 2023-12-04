package com.diegusmich.intouch.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ProfileLayoutBinding
import com.diegusmich.intouch.service.CloudImageService
import com.diegusmich.intouch.ui.activities.AuthActivity
import com.diegusmich.intouch.ui.viewmodels.ProfileViewModel
import com.google.android.material.appbar.MaterialToolbar

class ProfileFragment : BaseFragment() {

    private var _binding: ProfileLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: MaterialToolbar

    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = ProfileLayoutBinding.inflate(layoutInflater, container, false)
        toolbar = binding.appBarLayout.materialToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadAuthProfile()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadAuthProfile()
        }

        toolbar.title = getString(R.string.profile_title)
        toolbar.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_auth_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.editUserMenuOption -> {
                        false
                    }

                    R.id.logoutUserMenuOption -> {
                        viewModel.onLogout()
                        requireActivity().startActivity(
                            Intent(
                                requireContext(),
                                AuthActivity::class.java
                            ).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                        requireActivity().finish()
                        false
                    }

                    else -> false
                }
            }

        }, viewLifecycleOwner)
    }

    override fun lifecycleStateObserve() {
        viewModel.profile.observe(viewLifecycleOwner){
            if(it == null)
                return@observe

            toolbar.title = it.username
            binding.userImageProfile.load(CloudImageService.USERS.imageRef(it.img))
            binding.nameProfileLayout.text = it.name
            binding.biographyProfileLayout.text = it.biography
            binding.userInfoFriendship.setInfoValue(it.friends)
            binding.userInfoCreated.setInfoValue(it.created)
            binding.userInfoJoined.setInfoValue(it.joined)
        }

        viewModel.LOADING.observe(this){
            binding.swipeRefreshLayout.isRefreshing = it
        }

        viewModel.ERROR.observe(this){
            if (it != null)
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT)
                    .show()
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