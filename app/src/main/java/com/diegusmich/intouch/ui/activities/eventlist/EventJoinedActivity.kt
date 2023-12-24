package com.diegusmich.intouch.ui.activities.eventlist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.diegusmich.intouch.R
import com.diegusmich.intouch.ui.adapters.EventsListAdapter
import com.diegusmich.intouch.ui.viewmodels.EventCreatedViewModel
import com.diegusmich.intouch.ui.viewmodels.EventJoinedViewModel

class EventJoinedActivity : EventListActivity() {

    private val viewModel : EventJoinedViewModel by viewModels()
    private var userId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.title = getString(R.string.event_joined_activity)

        userId = intent.extras?.getString(USER_ARG)

        binding.eventListSwipeRefreshLayout.setOnRefreshListener {
            viewModel.onLoadJoinedEvents(userId, true)
        }

        binding.eventListRecyclerView.adapter = EventsListAdapter(listOf())

        viewModel.onLoadJoinedEvents(userId)
    }

    override fun lifecycleStateObserve() {

        viewModel.events.observe(this) {
            if (!it.isNullOrEmpty())
                (binding.eventListRecyclerView.adapter as EventsListAdapter).replace(it)
        }

        viewModel.LOADING.observe(this) {
            binding.eventListSwipeRefreshLayout.isRefreshingDelayed(this, it)
        }

        viewModel.ERROR.observe(this) {
            if (it != null)
                Toast.makeText(this, getString(it), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    companion object{
        const val USER_ARG = "userId"
    }
}