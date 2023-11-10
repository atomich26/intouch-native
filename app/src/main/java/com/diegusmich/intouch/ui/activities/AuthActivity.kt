package com.diegusmich.intouch.ui.activities

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.diegusmich.intouch.R

class AuthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.navigation_base_activity)

        val navController = supportFragmentManager.findFragmentById(R.id.fragment_nav_host)?.findNavController()
        navController?.setGraph(R.navigation.auth_navigation)
    }

    override fun lifecycleStateObserve() {
        return
    }
}