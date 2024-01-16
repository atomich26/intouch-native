package com.diegusmich.intouch.ui.activities

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.diegusmich.intouch.R
import com.diegusmich.intouch.broadcast.LocationBroadcasterReceiver
import com.diegusmich.intouch.databinding.ActivityMainBinding
import com.diegusmich.intouch.providers.NotificationProvider
import com.diegusmich.intouch.ui.adapters.MainViewPagerAdapter
import com.diegusmich.intouch.ui.fragments.CategoriesFragment
import com.diegusmich.intouch.ui.fragments.FeedFragment
import com.diegusmich.intouch.ui.fragments.NotificationFragment
import com.diegusmich.intouch.ui.fragments.ProfileFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!

    private var _onCameraPicturePicked: ((result: Boolean) -> Unit)? = null

    private lateinit var locationBroadcastReceiver: LocationBroadcasterReceiver

    val pickImageFromCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            _onCameraPicturePicked?.invoke(it)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }

        locationBroadcastReceiver = LocationBroadcasterReceiver(this)
        registerReceiver(
            locationBroadcastReceiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val mainViewPagerAdapter = MainViewPagerAdapter(
                arrayOf(
                    FeedFragment(),
                    CategoriesFragment(),
                    NotificationFragment(),
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

        createNotificationChannel()
        requestPermission()
        binding.mainBottomNavigation.setOnItemSelectedListener {

            if (it.itemId == R.id.navigation_create) {
                // Launch create event activity
                return@setOnItemSelectedListener false
            }

            val pageId = when (it.itemId) {
                R.id.feedFragment -> 0
                R.id.categoriesFragment -> 1
                R.id.notificationsFragment -> 2
                R.id.profileFragment -> 3
                else -> 0
            }

            binding.mainViewPager.setCurrentItem(pageId, false)
            true
        }
    }

    fun addOnCameraPicturePickedCallback(callback: (Boolean) -> Unit) {
        _onCameraPicturePicked = callback
    }

    private fun requestPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    //Mostro un warning sulla posizione approssimativa
                }

                else -> {
                    //Altrimenti mostro un errore sulla posizione
                }
            }
        }
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun createNotificationChannel() {
        NotificationProvider.createChannel(
            getString(R.string.notification_friendship_ch),
            getString(R.string.notification_friendship_id),
            getString(R.string.notification_friendship_desc),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        NotificationProvider.createChannel(
            getString(R.string.notification_event_ch),
            getString(R.string.notification_event_id),
            getString(R.string.notification_event_desc),
            NotificationManager.IMPORTANCE_HIGH
        )
        NotificationProvider.createChannel(
            getString(R.string.notification_post_comment_ch),
            getString(R.string.notification_post_comment_id),
            getString(R.string.notification_post_comment_desc),
            NotificationManager.IMPORTANCE_LOW
        )


        //TEST
        val builder =
            NotificationCompat.Builder(this, getString(R.string.notification_friendship_id))
                .setSmallIcon(R.drawable.baseline_group_24_white)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(12, builder.build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        unregisterReceiver(locationBroadcastReceiver)
    }
}