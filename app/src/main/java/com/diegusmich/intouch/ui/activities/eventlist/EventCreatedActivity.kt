package com.diegusmich.intouch.ui.activities.eventlist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.diegusmich.intouch.R
import com.diegusmich.intouch.ui.adapters.EventsListAdapter
import com.diegusmich.intouch.ui.adapters.UsersListAdapter
import com.diegusmich.intouch.ui.viewmodels.EventCreatedViewModel

class EventCreatedActivity : EventListActivity() {

    private val viewModel : EventCreatedViewModel by viewModels()
    private var userId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = intent.extras?.getString(USER_ARG)

        toolbar.title = getString(R.string.event_created_activity)
        binding.eventListSwipeRefreshLayout.setOnRefreshListener {
            viewModel.onLoadCreatedEvents(userId, true)
        }

        binding.eventListRecyclerView.adapter = EventsListAdapter(listOf())

        viewModel.onLoadCreatedEvents(userId)
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

    companion object {
        const val USER_ARG = "userId"
    }
}