package com.diegusmich.intouch.ui.activities.eventlist

import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.EventListLayoutBinding
import com.diegusmich.intouch.ui.activities.BaseActivity
import com.google.android.material.appbar.MaterialToolbar

abstract class EventListActivity : BaseActivity() {

    private var _binding: EventListLayoutBinding? = null
    protected val binding get() = _binding!!

    protected lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = EventListLayoutBinding.inflate(layoutInflater)

        toolbar = binding.appBarLayout.materialToolbar

        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        toolbar.navigationIcon =
            AppCompatResources.getDrawable(this, R.drawable.baseline_arrow_back_24)

        setContentView(binding.root)

        binding.eventListRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}