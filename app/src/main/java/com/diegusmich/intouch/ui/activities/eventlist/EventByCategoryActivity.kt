package com.diegusmich.intouch.ui.activities.eventlist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.diegusmich.intouch.ui.adapters.EventsListAdapter
import com.diegusmich.intouch.ui.viewmodels.EventCategoryViewModel

class EventByCategoryActivity : EventListActivity() {

    private val viewModel: EventCategoryViewModel by viewModels()
    private var categoryId: String? = null
    private var categoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryId = intent.extras?.getString(CATEGORY_ID_ARG)
        categoryName = intent.extras?.getString(CATEGORY_NAME_ARG)

        toolbar.title = categoryName
        binding.eventListSwipeRefreshLayout.setOnRefreshListener {
            viewModel.onLoadEventsByCategory(categoryId, true)
        }

        binding.eventListRecyclerView.adapter = EventsListAdapter(listOf())

        viewModel.onLoadEventsByCategory(categoryId)

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
        const val CATEGORY_ID_ARG = "catId"
        const val CATEGORY_NAME_ARG = "catName"
    }
}