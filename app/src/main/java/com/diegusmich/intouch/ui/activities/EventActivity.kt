package com.diegusmich.intouch.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diegusmich.intouch.R

class EventActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
    }

    override fun lifecycleStateObserve() {

    }

    companion object {
        const val EVENT_ARG = "eventId"
    }
}