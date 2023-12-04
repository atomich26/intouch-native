package com.diegusmich.intouch

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.diegusmich.intouch.service.NetworkService
import com.google.firebase.FirebaseApp
import java.util.TimeZone

class IntouchApplication : Application(), LifecycleEventObserver {

    companion object {
        private var _isAppForeground: Boolean = false
        val isAppForeground get() = _isAppForeground
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        NetworkService.buildService(this).observe()
        FirebaseApp.initializeApp(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        _isAppForeground = when (event) {
            Lifecycle.Event.ON_START -> true
            Lifecycle.Event.ON_STOP -> false
            else -> return
        }
    }
}