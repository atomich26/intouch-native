package com.diegusmich.intouch.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Friendship
import com.diegusmich.intouch.databinding.ProfileLayoutBinding
import com.diegusmich.intouch.service.CloudImageService
import com.diegusmich.intouch.ui.fragments.ModalPreferencesBottomSheet
import com.diegusmich.intouch.ui.viewmodels.ProfileViewModel
import com.google.android.material.appbar.MaterialToolbar

class UserActivity : BaseActivity() {

    private var _binding: ProfileLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: MaterialToolbar

    private val viewModel: ProfileViewModel by viewModels()
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        userId = intent.extras?.getString(USER_ARG)

        super.onCreate(savedInstanceState)

        _binding = ProfileLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.appBarLayout.materialToolbar
        toolbar.title = getString(R.string.profile_title)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        toolbar.navigationIcon =
            AppCompatResources.getDrawable(this, R.drawable.baseline_arrow_back_24)

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onLoadData(this.userId, true)
        }

        val prefsModalBottomSheet = ModalPreferencesBottomSheet()
        binding.showUserPrefButton.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag(ModalPreferencesBottomSheet.TAG) == null)
                prefsModalBottomSheet.show(supportFragmentManager, ModalPreferencesBottomSheet.TAG)
        }

        binding.userInfoFriendship.setOnClickListener {
            with(viewModel.userProfile) {
                if (value?.friends?.compareTo(0) == 1) {
                    startActivity(Intent(this@UserActivity, UserFriendsActivity::class.java).apply {
                        putExtra(UserFriendsActivity.USER_ARG, value?.id)
                    })
                }
            }
        }

        viewModel.onLoadData(this.userId)
    }


    override fun lifecycleStateObserve() {
        viewModel.userProfile.observe(this) {
            if (it == null)
                return@observe

            toolbar.title = it.username
            binding.userImageProfile.load(CloudImageService.USERS.imageRef(it.img))
            binding.nameProfileLayout.text = it.name
            binding.biographyProfileLayout.text = it.biography
            binding.userInfoFriendship.setInfoValue(it.friends)
            binding.userInfoCreated.setInfoValue(it.created)
            binding.userInfoJoined.setInfoValue(it.joined)

            when (it.friendship.status) {
                is Friendship.Status.PENDING -> {
                    val isVisible = if (it.friendship.status.isActor) View.GONE else View.VISIBLE
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

        viewModel.LOADING.observe(this) {
            binding.swipeRefreshLayout.isRefreshingDelayed(this, it)
        }

        viewModel.ERROR.observe(this) {
            if (it != null)
                Toast.makeText(this, getString(it), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val USER_ARG = "userId"
    }
}