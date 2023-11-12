package com.diegusmich.intouch.ui.activities

import android.os.Bundle
import com.diegusmich.intouch.R

class AuthActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_auth)
    }

    override fun lifecycleStateObserve() {
        return
    }
}