package com.diegusmich.intouch.ui.activities

import android.content.Intent
import android.os.Bundle
import com.diegusmich.intouch.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_base_activity)

       if(Firebase.auth.currentUser == null){
           startActivity(Intent(this, AuthActivity::class.java).apply {
               flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
           })
           finish()
        }
    }

    override fun lifecycleStateObserve() {
        return
    }
}