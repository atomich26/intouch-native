package com.diegusmich.intouch

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.diegusmich.intouch.providers.NetworkProvider
import com.diegusmich.intouch.providers.NotificationProvider
import com.diegusmich.intouch.providers.UserLocationProvider
import com.google.firebase.FirebaseApp

class IntouchApplication : Application(), LifecycleEventObserver {

    companion object {
        private var _isAppForeground: Boolean = false
        val isAppForeground get() = _isAppForeground
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        NetworkProvider.build(this)
        NotificationProvider.build(this)
        UserLocationProvider.build(this)
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