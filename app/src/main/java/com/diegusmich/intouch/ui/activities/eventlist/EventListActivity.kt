package com.diegusmich.intouch.ui.activities.eventlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.EventListActivityBinding
import com.diegusmich.intouch.ui.fragments.ProfileFragment
import com.google.android.material.appbar.MaterialToolbar

abstract class EventListActivity : AppCompatActivity() {

    private var _binding: EventListActivityBinding? = null
    protected val binding get() = _binding!!

    protected lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = EventListActivityBinding.inflate(layoutInflater)

        toolbar = binding.appBarLayout.materialToolbar
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        toolbar.navigationIcon =
            AppCompatResources.getDrawable(this, R.drawable.baseline_arrow_back_24)

        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.eventListLayoutHost, instanceFragmentLayout())
            }
        }
    }

    protected abstract fun instanceFragmentLayout() : Fragment

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}