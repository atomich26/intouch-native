package com.diegusmich.intouch.ui.fragments

import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Friendship
import com.diegusmich.intouch.databinding.ProfileLayoutBinding
import com.diegusmich.intouch.providers.CloudImageProvider
import com.diegusmich.intouch.ui.activities.AuthActivity
import com.diegusmich.intouch.ui.activities.EditUserActivity
import com.diegusmich.intouch.ui.activities.UserFriendsActivity
import com.diegusmich.intouch.ui.activities.eventlist.EventCreatedActivity
import com.diegusmich.intouch.ui.activities.eventlist.EventJoinedActivity
import com.diegusmich.intouch.ui.adapters.ArchivedPostsAdapter
import com.diegusmich.intouch.ui.viewmodels.ProfileViewModel
import com.google.android.material.appbar.MaterialToolbar
import java.io.File

private const val USER_ID_ARG: String = "userId"
private const val ACTIVITY_WRAPPED_ARG: String = "wrapped"

class ProfileFragment : Fragment() {

    private var _binding: ProfileLayoutBinding? = null
    val binding get() = _binding!!

    private var userIdArg: String? = null
    private var activityWrappedArg: Boolean? = null

    private lateinit var toolbar: MaterialToolbar
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userIdArg = it.getString(USER_ID_ARG)
            activityWrappedArg = it.getBoolean(ACTIVITY_WRAPPED_ARG)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        activityWrappedArg?.let { outState.putBoolean(ACTIVITY_WRAPPED_ARG, it) }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getBoolean(ACTIVITY_WRAPPED_ARG)?.let {
            activityWrappedArg = it
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

        toolbar.title = getString(R.string.profile_title)
        activityWrappedArg?.let {
            if (it) {
                toolbar.apply {
                    setNavigationOnClickListener {
                        activity?.onBackPressedDispatcher?.onBackPressed()
                    }
                    navigationIcon =
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.baseline_arrow_back_24
                        )
                }
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onLoadUserData(userIdArg, true)
        }

        binding.userImageProfile.setOnClickListener {
            if(viewModel.isAuth.value!! && activityWrappedArg == false){
                val mediaPickModalBottomSheet = MediaPickModalBottomSheet.newInstance(viewModel.image.value)
                requireActivity().supportFragmentManager.let {
                    if (it.findFragmentByTag(MediaPickModalBottomSheet.TAG) == null)
                        mediaPickModalBottomSheet.show(it, MediaPickModalBottomSheet.TAG)
                }

            }else{
                viewModel.image.value?.let { imagePath ->
                    if(imagePath.isBlank())
                        return@let

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
                }
            }
        }

        binding.showUserPrefButton.setOnClickListener {
            viewModel.id.value?.let {
                val prefsModalBottomSheet = PreferencesModalBottomSheet().apply {
                    val prefsNameArray =
                        viewModel.preferences.value?.map { it.name }?.toTypedArray()
                    arguments = bundleOf(
                        PreferencesModalBottomSheet.PREFS_ARRAY to prefsNameArray,
                        PreferencesModalBottomSheet.CAN_EDIT_ARG to (viewModel.isAuth.value == true && activityWrappedArg == false)
                    )
                }
                val fragmentManager = requireActivity().supportFragmentManager

                if (fragmentManager.findFragmentByTag(PreferencesModalBottomSheet.TAG) == null)
                    prefsModalBottomSheet.show(fragmentManager, PreferencesModalBottomSheet.TAG)
            }
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

        binding.addFriendButton.setOnClickListener {
            viewModel.onSendFriendshipRequest()
        }

        binding.removeFriendButton.setOnClickListener {
            viewModel.onRemoveFriend()
        }

        binding.acceptFriendshipButton.setOnClickListener {
            viewModel.onHandleFriendshipRequest(true)
        }

        binding.denyFriendshipButton.setOnClickListener {
            viewModel.onHandleFriendshipRequest(false)
        }

        binding.editProfileButton.setOnClickListener {
            requireContext().startActivity(
                Intent(
                    requireContext(),
                    EditUserActivity::class.java
                )
            )
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
            it?.let {
                toolbar.title = it
            }
        }

        viewModel.biography.observe(viewLifecycleOwner) {
            if (it?.isNotBlank() == true) {
                binding.biographyProfileLayout.text = it
                binding.biographyProfileLayout.visibility = View.VISIBLE
            } else
                binding.biographyProfileLayout.visibility = View.GONE
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

        viewModel.isAuth.observe(viewLifecycleOwner) {
            if (it) {
                if (toolbar.menu.isEmpty() && activityWrappedArg == false)
                    setAuthToolbarMenu()
            }
        }

        viewModel.friendship.observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    is Friendship.Status.AUTH -> {
                        binding.editProfileButton.visibility = View.VISIBLE
                    }
                    is Friendship.Status.PENDING -> {
                        val bannerVisibility = if (it.status.isActor) View.GONE else View.VISIBLE
                        binding.friendshipRequestBanner.visibility = bannerVisibility
                        binding.addFriendButton.apply {
                            visibility = View.VISIBLE
                            isEnabled = false
                            if(it.status.isActor)
                                text = getString(R.string.pending_request_button)
                        }
                        binding.removeFriendButton.visibility  = View.GONE
                    }

                    is Friendship.Status.FRIEND -> {
                        binding.friendshipRequestBanner.visibility = View.GONE
                        binding.removeFriendButton.visibility  = View.VISIBLE
                        binding.addFriendButton.visibility = View.GONE
                    }

                    is Friendship.Status.NONE -> {
                        binding.friendshipRequestBanner.visibility = View.GONE
                        binding.addFriendButton.apply {
                            visibility = View.VISIBLE
                            isEnabled = true
                            text = getString(R.string.user_add_friendship)
                        }
                        binding.removeFriendButton.visibility = View.GONE
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

        viewModel.LOGGED_OUT.observe(viewLifecycleOwner){
            if(it){
                requireActivity().startActivity(
                    Intent(
                        requireContext(),
                        AuthActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                requireActivity().finish()
            }
        }
        viewModel.archivedPosts.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty())
                (binding.userPostsGridView.adapter as ArchivedPostsAdapter).replace(it)
        }

        viewModel.LOADING.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshingDelayed(viewLifecycleOwner, it, 0)
            binding.showUserPrefButton.isEnabled = !it
            binding.removeFriendButton.isEnabled = !it
        }

        viewModel.ERROR.observe(viewLifecycleOwner) {
            if (it != null)
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun setAuthToolbarMenu() {
        toolbar.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_auth_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logoutUserMenuOption -> {
                        viewModel.onLogout()
                        false
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun loadProfileImage(imagePath: String, isAuth: Boolean) {
        binding.userImageProfileOverlay.visibility =
            if (!activityWrappedArg!! && isAuth) View.VISIBLE else View.GONE
        binding.userImageProfileContent.load(CloudImageProvider.USERS.imageRef(imagePath))
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
                arguments = bundleOf(USER_ID_ARG to userId, ACTIVITY_WRAPPED_ARG to activityWrapped)
            }
    }
}