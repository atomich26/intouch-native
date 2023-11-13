package com.diegusmich.intouch.ui.activities.main

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivityMainBinding
import com.diegusmich.intouch.ui.activities.AuthActivity
import com.diegusmich.intouch.ui.activities.BaseActivity
import com.diegusmich.intouch.ui.adapters.MainViewPagerAdapter
import com.diegusmich.intouch.ui.fragments.categories.CategoriesFragment
import com.diegusmich.intouch.ui.fragments.feed.FeedFragment
import com.diegusmich.intouch.ui.fragments.notifications.NotificationFragment
import com.diegusmich.intouch.ui.fragments.profile.ProfileFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val mainViewPagerAdapter = MainViewPagerAdapter(
                arrayOf(
                    FeedFragment(),
                    CategoriesFragment(),
                    NotificationFragment(),
                    ProfileFragment()
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

        binding.mainBottomNavigation.setOnItemSelectedListener {
            val pageId = when (it.itemId) {
                R.id.feedFragment -> 0
                R.id.categoriesFragment -> 1
                R.id.notificationsFragment -> 2
                R.id.profileFragment -> 3
                else -> 0
            }

            binding.mainViewPager.setCurrentItem(pageId, true)
            true
        }
    }

    override fun lifecycleStateObserve() {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}