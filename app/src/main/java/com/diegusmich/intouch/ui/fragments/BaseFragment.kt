package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Osserva il viewModel assegnato
        lifecycleStateObserve()
    }

    protected abstract fun lifecycleStateObserve()
}