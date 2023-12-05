package com.diegusmich.intouch.ui.activities

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
import com.diegusmich.intouch.ui.adapters.SearchResultsAdapter
import com.diegusmich.intouch.ui.viewmodels.SearchActivityViewModel
import com.diegusmich.intouch.ui.views.decorators.visible

class SearchActivity : BaseActivity() {

    private var _binding : ActivitySearchBinding? = null
    private val binding get() = _binding!!

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
        binding.searchResultsRecyclerView.adapter = SearchResultsAdapter(mutableListOf())
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
                    val queryText = query ?: ""

                    if(queryText.replace("@", "").isNullOrBlank()){
                        Toast.makeText(this@SearchActivity, getString(R.string.search_empty_text), Toast.LENGTH_SHORT).show()
                        return false
                    }

                    if(query?.startsWith("@") == true)
                        viewModel.onSearchByUsername(queryText.replace("@", ""))
                    else
                        viewModel.onSearchByEvent(queryText)

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

        viewModel.searchUserResult.observe(this){
            if(viewModel.CONTENT_LOADED.value == true){
               if(!it.isNullOrEmpty()){
                   (binding.searchResultsRecyclerView.adapter as SearchResultsAdapter).replace(it)
               }
               else
                   Toast.makeText(this@SearchActivity, getString(R.string.search_users_not_found), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.LOADING.observe(this){
            binding.pgLayout.progressBar.visible(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}