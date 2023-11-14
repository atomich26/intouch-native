package com.diegusmich.intouch.ui.activities.search

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.UserPreview
import com.diegusmich.intouch.databinding.ActivitySearchBinding
import com.diegusmich.intouch.ui.activities.BaseActivity
import com.diegusmich.intouch.ui.adapters.SearchResultsAdapter
import com.diegusmich.intouch.ui.state.UiState
import com.diegusmich.intouch.ui.views.decorators.visible
import kotlinx.coroutines.launch


class SearchActivity : BaseActivity() {

    private var _binding : ActivitySearchBinding? = null
    private val binding get() = _binding!!

    private val searchByUsernameRegex = Regex("^@.+")

    private val viewModel : SearchActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val toolbar = binding.appBarLayout.materialToolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        toolbar.navigationIcon =  AppCompatResources.getDrawable(this, R.drawable.baseline_arrow_back_24)

        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(this).apply {
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

            setOnQueryTextListener(object: OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(query.isNullOrBlank())
                        return false

                    if(searchByUsernameRegex.matches(query))
                        viewModel.onSearchByUsername(query)
                    else
                        viewModel.onSearchByEvent(query)

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(newText.isNullOrBlank()){
                        (binding.searchResultsRecyclerView.adapter as SearchResultsAdapter).clear()
                    }
                    return false
                }
            })
        }
        return true
    }

    override fun lifecycleStateObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){

                launch {
                    viewModel.searchUserResult.collect{
                        if(it != null)
                            binding.searchResultsRecyclerView.adapter?.notifyDataSetChanged()
                    }
                }

                viewModel.uiState.collect{ it ->
                    binding.pgLayout.progressBar.visible(it is UiState.LOADING)
                    when(it){

                        else -> Unit
                    }
                    viewModel.consumeEvent()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}