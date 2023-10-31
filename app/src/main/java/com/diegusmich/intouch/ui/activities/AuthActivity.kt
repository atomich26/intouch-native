package com.diegusmich.intouch.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.diegusmich.intouch.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.navigation_base_activity)

        val navController = supportFragmentManager.findFragmentById(R.id.fragment_nav_host)?.findNavController()
        navController?.setGraph(R.navigation.auth_navigation)

    }
}