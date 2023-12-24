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
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ProfileLayoutBinding
import com.diegusmich.intouch.service.CloudImageService
import com.diegusmich.intouch.ui.activities.AuthActivity
import com.diegusmich.intouch.ui.activities.UserFriendsActivity
import com.diegusmich.intouch.ui.activities.eventlist.EventCreatedActivity
import com.diegusmich.intouch.ui.activities.eventlist.EventJoinedActivity
import com.diegusmich.intouch.ui.adapters.ArchivedPostsAdapter
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

        binding.userProfileButtonGroup.visibility = View.GONE
        viewModel.onLoadAuthData()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onLoadAuthData(true)
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

        binding.userImageProfile.setOnLongClickListener {
            viewModel.userProfile.value?.img.let { imagePath ->

                if (imagePath.isNullOrBlank())
                    return@let false

                requireActivity().supportFragmentManager.let { fragmentManager ->
                    ProfileImageFragmentDialog.newInstance(imagePath).let { fragment ->
                        fragmentManager.beginTransaction().apply {
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            add(fragment, "PROFILE_IMAGE_FRAGMENT_DIALOG")
                            addToBackStack(null)
                            commit()
                        }
                    }
                }
                true
            }
        }

        binding.userInfoFriendship.setOnClickListener {
            with(viewModel.userProfile) {
                if (value?.friends?.compareTo(0) == 1) {
                    startActivity(Intent(requireContext(), UserFriendsActivity::class.java).apply {
                        putExtra(UserFriendsActivity.USER_ARG, value?.id)
                    })
                }
            }
        }

        binding.userInfoCreated.setOnClickListener {
            with(viewModel.userProfile) {
                if (value?.created?.compareTo(0) == 1) {
                    startActivity(Intent(requireContext(), EventCreatedActivity::class.java).apply {
                        putExtra(EventCreatedActivity.USER_ARG, value?.id)
                    })
                }
            }
        }

        binding.userInfoJoined.setOnClickListener {
            with(viewModel.userProfile) {
                if (value?.joined?.compareTo(0) == 1) {
                    startActivity(Intent(requireContext(), EventJoinedActivity::class.java).apply {
                        putExtra(EventJoinedActivity.USER_ARG, value?.id)
                    })
                }
            }
        }

        binding.userPostsGridView.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

        binding.userPostsGridView.adapter = ArchivedPostsAdapter(viewModel.archivedPosts.value!!)
    }

    override fun lifecycleStateObserve() {
        viewModel.userProfile.observe(viewLifecycleOwner) {
            if (it == null)
                return@observe

            toolbar.title = it.username

            if (it.img.isNotBlank())
                binding.userImageProfile.load(CloudImageService.USERS.imageRef(it.img))

            binding.nameProfileLayout.text = it.name
            binding.biographyProfileLayout.text = it.biography
            binding.userInfoFriendship.setInfoValue(it.friends)
            binding.userInfoCreated.setInfoValue(it.created)
            binding.userInfoJoined.setInfoValue(it.joined)
        }

        viewModel.archivedPosts.observe(this) {
            if (!it.isNullOrEmpty())
                (binding.userPostsGridView.adapter as ArchivedPostsAdapter).replace(it)
        }

        viewModel.LOADING.observe(this) {
            binding.swipeRefreshLayout.isRefreshingDelayed(viewLifecycleOwner, it)
        }

        viewModel.ERROR.observe(this) {
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