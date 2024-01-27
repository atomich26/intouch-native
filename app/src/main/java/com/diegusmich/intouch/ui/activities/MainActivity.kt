package com.diegusmich.intouch.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivityMainBinding
import com.diegusmich.intouch.providers.AuthProvider
import com.diegusmich.intouch.providers.NotificationProvider
import com.diegusmich.intouch.ui.adapters.MainViewPagerAdapter
import com.diegusmich.intouch.ui.fragments.CategoriesFragment
import com.diegusmich.intouch.ui.fragments.FeedFragment
import com.diegusmich.intouch.ui.fragments.FriendshipRequestsFragment
import com.diegusmich.intouch.ui.fragments.ProfileFragment
import com.diegusmich.intouch.ui.viewmodels.FriendshipRequestViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!

    val friendshipRequestViewModel: FriendshipRequestViewModel by viewModels()

    private var _onCameraPicturePicked: ((result: Boolean) -> Unit)? = null

    val pickImageFromCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            _onCameraPicturePicked?.invoke(it)
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (AuthProvider.authUser() == null) {
            startActivity(Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }else{
            initMainFragments()
            listenForFriendshipUpdate()
            requestPermission()
            createNotificationChannels()

            binding.mainBottomNavigation.setOnItemSelectedListener {

                if (it.itemId == R.id.navigation_create) {
                    // Launch create event activity
                    return@setOnItemSelectedListener false
                }

                val pageId = when (it.itemId) {
                    R.id.feedFragment -> 0
                    R.id.categoriesFragment -> 1
                    R.id.friendshipRequestsFragment -> 2
                    R.id.profileFragment -> 3
                    else -> 0
                }

                binding.mainViewPager.setCurrentItem(pageId, false)
                true
            }
        }
    }

    private fun listenForFriendshipUpdate() {
        friendshipRequestViewModel.listenForFriendshipRequestsUpdate()
        friendshipRequestViewModel.friendshipRequests.observe(this) {
            addFriendshipIconBadge(it.size)
        }
    }

    private fun initMainFragments() = lifecycleScope.launch {
        val friendshipRequestsFragment = FriendshipRequestsFragment()
        val mainViewPagerAdapter = MainViewPagerAdapter(
            arrayOf(
                FeedFragment(),
                CategoriesFragment(),
                friendshipRequestsFragment,
                ProfileFragment.newInstance(Firebase.auth.currentUser?.uid, false)
            ), supportFragmentManager, lifecycle
        )

        binding.mainViewPager.adapter = mainViewPagerAdapter
        binding.mainViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val fixedPosition = if (position > 1) position + 1 else position
                binding.mainBottomNavigation.menu.getItem(fixedPosition).isChecked = true
            }
        })
    }

    fun addOnCameraPicturePickedCallback(callback: (Boolean) -> Unit) {
        _onCameraPicturePicked = callback
    }

    private fun addFriendshipIconBadge(value: Int) {
        if (value > 0) {
            val badge =
                binding.mainBottomNavigation.getOrCreateBadge(R.id.friendshipRequestsFragment)
            badge.number = value
        } else
            binding.mainBottomNavigation.removeBadge(R.id.friendshipRequestsFragment)
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { _ -> return@registerForActivityResult }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun createNotificationChannels() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        sharedPref.getBoolean(getString(R.string.notification_created_pref_key), false).let {
            if (!it) {
                NotificationProvider.Channel.FRIENDSHIP.create(this)
                NotificationProvider.Channel.EVENT.create(this)
                NotificationProvider.Channel.COMMENT.create(this)
                with(sharedPref.edit()) {
                    putBoolean(getString(R.string.notification_created_pref_key), true)
                    apply()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}