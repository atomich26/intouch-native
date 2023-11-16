package com.diegusmich.intouch.ui.activities.search

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivitySearchBinding
import com.diegusmich.intouch.ui.activities.BaseActivity
import com.diegusmich.intouch.ui.adapters.SearchResultsAdapter


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

        viewModel.ERROR.observe(this){
            if (it != null)
                Toast.makeText(this@SearchActivity, getString(it), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}