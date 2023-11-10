package com.diegusmich.intouch.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleStateObserve()
    }

    protected abstract fun lifecycleStateObserve()
}