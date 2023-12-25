package com.diegusmich.intouch.ui.activities

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivitySearchBinding
import com.diegusmich.intouch.ui.adapters.EventsListAdapter
import com.diegusmich.intouch.ui.adapters.MutableAdapter
import com.diegusmich.intouch.ui.adapters.UsersListAdapter
import com.diegusmich.intouch.ui.viewmodels.SearchActivityViewModel
import com.diegusmich.intouch.ui.views.decorators.visible

class SearchActivity : BaseActivity() {

    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchListView: RecyclerView

    private val viewModel: SearchActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        searchListView = binding.searchResultsRecyclerView

        setContentView(binding.root)

        val toolbar = binding.appBarLayout.materialToolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        toolbar.navigationIcon =
            AppCompatResources.getDrawable(this, R.drawable.baseline_arrow_back_24)

        searchListView.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.search_menu, menu)

        val searchView = menu?.getItem(0)?.actionView as SearchView
        searchView.apply {
            queryHint = getString(R.string.search_query_hint_text)
            setOnCloseListener { false }
            onActionViewExpanded()

            setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        if (it.startsWith("@"))
                            viewModel.onRunSearch(query, SearchActivityViewModel.SearchType.USERS)
                        else
                            viewModel.onRunSearch(query, SearchActivityViewModel.SearchType.EVENTS)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrBlank()) {
                        (binding.searchResultsRecyclerView.adapter as MutableAdapter<*>).clear()
                    }
                    return true
                }
            })
        }
        return true
    }

    override fun lifecycleStateObserve() {

        viewModel.ERROR.observe(this) {
            if (it != null)
                Toast.makeText(this@SearchActivity, getString(it), Toast.LENGTH_SHORT)
                    .show()
        }

        viewModel.searchUserResult.observe(this) {
            if(searchListView.adapter !is UsersListAdapter)
                searchListView.adapter = UsersListAdapter(it)
            else
                (searchListView.adapter as UsersListAdapter).replace(it)
        }

        viewModel.searchEventResult.observe(this){
            if(searchListView.adapter !is EventsListAdapter)
                searchListView.adapter = EventsListAdapter(it)
            else
                (searchListView.adapter as EventsListAdapter).replace(it)

        }

        viewModel.CONTENT_LOADED.observe(this) {

        }

        viewModel.LOADING.observe(this) {
            binding.pgLayout.progressBar.visible(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}