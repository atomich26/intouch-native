package com.diegusmich.intouch.ui.activities

import android.os.Bundle
import androidx.fragment.app.commit
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivityUserBinding
import com.diegusmich.intouch.databinding.ProfileLayoutBinding
import com.diegusmich.intouch.ui.fragments.ModalPreferencesBottomSheet
import com.diegusmich.intouch.ui.fragments.ProfileFragment
import com.google.android.material.appbar.MaterialToolbar

class UserActivity : BaseActivity() {

    private var _binding: ActivityUserBinding? = null
    private val binding get() = _binding!!

    private var userIdArg: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserBinding.inflate(layoutInflater)

        setContentView(binding.root)

        userIdArg = intent.extras?.getString(USER_ARG)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.profileLayoutHost, ProfileFragment.newInstance(userIdArg))
            }
        }
    }


    override fun lifecycleStateObserve() {
        return
        /*    viewModel.userProfile.observe(this) {
                if (it == null)
                    return@observe


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
            } */
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val USER_ARG = "userId"
    }
}