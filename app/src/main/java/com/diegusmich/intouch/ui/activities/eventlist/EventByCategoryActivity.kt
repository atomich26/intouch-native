package com.diegusmich.intouch.ui.activities.eventlist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.diegusmich.intouch.ui.adapters.EventsListAdapter
import com.diegusmich.intouch.ui.fragments.EventListFragment
import com.diegusmich.intouch.ui.viewmodels.EventCategoryViewModel

class EventByCategoryActivity : EventListActivity() {

    private var categoryId: String? = null
    private var categoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        categoryId = intent.extras?.getString(CATEGORY_ID_ARG)
        categoryName = intent.extras?.getString(CATEGORY_NAME_ARG)

        super.onCreate(savedInstanceState)
        toolbar.title = categoryName
    }

    override fun instanceFragmentLayout(): Fragment {
        return EventListFragment.newInstance(categoryId, EventListFragment.EventsFilterType.BY_CATEGORIES)
    }

    companion object {
        const val CATEGORY_ID_ARG = "catId"
        const val CATEGORY_NAME_ARG = "catName"
    }
}