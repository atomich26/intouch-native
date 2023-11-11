package com.diegusmich.intouch.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : BaseActivity() {

    private var _binding : ActivityMainBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Firebase.auth.currentUser == null){
           startActivity(Intent(this, AuthActivity::class.java).apply {
               flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
           })
           finish()
        }

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host ) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.feedFragment, R.id.searchFragment, R.id.notificationsFragment, R.id.profileFragment))

        binding.mainBottomNavigation.setupWithNavController(navController)
        binding.mainActivityAppBar.setupWithNavController(navController, appBarConfiguration)

        setSupportActionBar(binding.mainActivityAppBar)
    }

    override fun lifecycleStateObserve() {
        return
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}