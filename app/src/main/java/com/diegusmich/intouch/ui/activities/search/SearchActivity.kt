package com.diegusmich.intouch.ui.activities.search

import android.os.Bundle
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivitySearchBinding
import com.diegusmich.intouch.ui.activities.BaseActivity

class SearchActivity : BaseActivity() {

    private var _binding : ActivitySearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }

    override fun lifecycleStateObserve() {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}