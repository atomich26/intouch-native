package com.diegusmich.intouch.ui.activities.search

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivitySearchBinding
import com.diegusmich.intouch.ui.activities.BaseActivity

class SearchActivity : BaseActivity() {

    private var _binding : ActivitySearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val toolbar = binding.searchToolbar.materialToolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        toolbar.navigationIcon =  AppCompatResources.getDrawable(this, R.drawable.baseline_arrow_back_24)
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
                    Toast.makeText(this@SearchActivity, "Ricerca in corso", Toast.LENGTH_SHORT).show()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
        return true
    }

    override fun lifecycleStateObserve() {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}