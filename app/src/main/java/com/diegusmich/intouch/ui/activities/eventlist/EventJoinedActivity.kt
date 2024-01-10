package com.diegusmich.intouch.ui.activities.eventlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.diegusmich.intouch.R
import com.diegusmich.intouch.ui.fragments.EventListFragment

class EventJoinedActivity : EventListActivity() {

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        userId = intent.extras?.getString(USER_ARG)

        super.onCreate(savedInstanceState)
        toolbar.title = getString(R.string.event_joined_activity)
    }

    override fun instanceFragmentLayout(): Fragment {
        return EventListFragment.newInstance(userId, EventListFragment.EventsFilterType.JOINED)
    }

    companion object {
        const val USER_ARG = "userId"
    }
}