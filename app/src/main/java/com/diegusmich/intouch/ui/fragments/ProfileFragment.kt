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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Friendship
import com.diegusmich.intouch.databinding.ProfileLayoutBinding
import com.diegusmich.intouch.service.CloudImageService
import com.diegusmich.intouch.ui.activities.AuthActivity
import com.diegusmich.intouch.ui.activities.UserFriendsActivity
import com.diegusmich.intouch.ui.activities.eventlist.EventCreatedActivity
import com.diegusmich.intouch.ui.activities.eventlist.EventJoinedActivity
import com.diegusmich.intouch.ui.adapters.ArchivedPostsAdapter
import com.diegusmich.intouch.ui.viewmodels.ProfileViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val USER_ID_ARG: String = "userId"
private const val ACTIVITY_WRAPPED = "wrapped"

class ProfileFragment : Fragment() {

    private var _binding: ProfileLayoutBinding? = null
    val binding get() = _binding!!

    private var isAuthProfileFragmentArg: Boolean = false
    private var userIdArg: String? = null
    private var activityWrapped: Boolean = false

    private lateinit var toolbar: MaterialToolbar
    private val viewModel: ProfileViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { bundle ->
            activityWrapped = bundle.getBoolean(ACTIVITY_WRAPPED, false)
            bundle.getString(USER_ID_ARG)?.let {
                isAuthProfileFragmentArg = it == Firebase.auth.currentUser?.uid
                userIdArg = it
            }
        }
    }

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

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onLoadUserData(userIdArg, true)
        }

        toolbar.title = getString(R.string.profile_title)

        if (activityWrapped) {
            toolbar.apply {
                setNavigationOnClickListener {
                    requireActivity().finish()
                }
                navigationIcon =
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.baseline_arrow_back_24
                    )
            }
        }

        if (isAuthProfileFragmentArg && !activityWrapped) {
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
        } else
            binding.userProfileButtonGroup.visibility = View.GONE

        binding.userImageProfile.setOnClickListener {
            viewModel.image.value?.let { imagePath ->
                requireActivity().supportFragmentManager.let { fragmentManager ->
                    ProfileImageFragmentDialog.newInstance(
                        imagePath,
                        isAuthProfileFragmentArg && !activityWrapped
                    ).let { fragment ->
                        fragmentManager.beginTransaction().apply {
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            add(fragment, "PROFILE_IMAGE_FRAGMENT_DIALOG")
                            addToBackStack(null)
                            commit()
                        }
                    }
                }
            }
        }

        binding.showUserPrefButton.setOnClickListener {
            val prefsModalBottomSheet = PreferencesModalBottomSheet().apply {
                val prefsNameArray = viewModel.preferences.value?.map { it.name }?.toTypedArray()
                arguments = bundleOf(PreferencesModalBottomSheet.PREFS_ARRAY to prefsNameArray)
            }
            val fragmentManager = requireActivity().supportFragmentManager

            if (fragmentManager.findFragmentByTag(PreferencesModalBottomSheet.TAG) == null)
                prefsModalBottomSheet.show(fragmentManager, PreferencesModalBottomSheet.TAG)
        }

        binding.userInfoFriendship.setOnClickListener { _ ->
            viewModel.friends.value?.let { friends ->
                if (friends > 0) {
                    startActivity(Intent(requireContext(), UserFriendsActivity::class.java).apply {
                        viewModel.id.value?.let {
                            putExtra(UserFriendsActivity.USER_ARG, it)
                        }
                    })
                }
            }
        }

        binding.userInfoCreated.setOnClickListener { _ ->
            viewModel.eventsCreated.value?.let { created ->
                if (created > 0) {
                    startActivity(Intent(requireContext(), EventCreatedActivity::class.java).apply {
                        viewModel.id.value?.let {
                            putExtra(EventCreatedActivity.USER_ARG, it)
                        }
                    })
                }
            }
        }

        binding.userInfoJoined.setOnClickListener { _ ->
            viewModel.eventsJoined.value?.let { joined ->
                if (joined > 0) {
                    startActivity(Intent(requireContext(), EventJoinedActivity::class.java).apply {
                        viewModel.id.value?.let {
                            putExtra(EventJoinedActivity.USER_ARG, it)
                        }
                    })
                }
            }
        }

        binding.userPostsGridView.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

        binding.userPostsGridView.adapter = ArchivedPostsAdapter(viewModel.archivedPosts.value!!)

        observeData()
        viewModel.onLoadUserData(userIdArg)
    }

    private fun observeData() {
        viewModel.name.observe(viewLifecycleOwner) {
            binding.nameProfileLayout.text = it
        }

        viewModel.username.observe(viewLifecycleOwner) {
            toolbar.title = it
        }

        viewModel.biography.observe(viewLifecycleOwner) {
            binding.biographyProfileLayout.text = it
        }

        viewModel.friends.observe(viewLifecycleOwner) {
            binding.userInfoFriendship.setInfoValue(it)
        }

        viewModel.eventsCreated.observe(viewLifecycleOwner) {
            binding.userInfoCreated.setInfoValue(it)
        }

        viewModel.eventsJoined.observe(viewLifecycleOwner) {
            binding.userInfoJoined.setInfoValue(it)
        }

        viewModel.friendship.observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    is Friendship.Status.PENDING -> {
                        val isVisible = if (it.status.isActor) View.GONE else View.VISIBLE
                        binding.friendshipRequestBanner.visibility = isVisible
                    }

                    is Friendship.Status.FRIEND -> {
                        binding.userProfileButtonGroup.visibility = View.VISIBLE
                    }

                    is Friendship.Status.NONE -> {
                        binding.friendshipRequestBanner.visibility = View.GONE
                        binding.userProfileButtonGroup.visibility = View.VISIBLE
                    }

                    else -> {

                    }
                }
            }
        }

        viewModel.image.observe(viewLifecycleOwner) { imagePath ->
            imagePath?.let {
                viewModel.isAuth.value?.let { isAuth ->
                    loadProfileImage(it, isAuth)
                }
            }
        }

        viewModel.archivedPosts.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty())
                (binding.userPostsGridView.adapter as ArchivedPostsAdapter).replace(it)
        }

        viewModel.LOADING.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshingDelayed(viewLifecycleOwner, it, 0)
        }

        viewModel.ERROR.observe(viewLifecycleOwner) {
            if (it != null)
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun loadProfileImage(imagePath: String, isAuth: Boolean) {
        binding.userImageProfileOverlay.visibility =
            if (isAuth && !activityWrapped) View.VISIBLE else View.GONE
        binding.userImageProfileContent.load(CloudImageService.USERS.imageRef(imagePath))
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

    companion object {
        @JvmStatic
        fun newInstance(userId: String?, activityWrapped: Boolean) =
            ProfileFragment().apply {
                arguments = bundleOf(USER_ID_ARG to userId, ACTIVITY_WRAPPED to activityWrapped)
            }
    }
}