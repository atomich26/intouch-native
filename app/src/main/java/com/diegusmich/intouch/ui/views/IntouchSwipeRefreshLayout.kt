package com.diegusmich.intouch.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.diegusmich.intouch.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IntouchSwipeRefreshLayout(ctx : Context, attrs : AttributeSet) : SwipeRefreshLayout(ctx, attrs){

    private var loadingAnimationTask : Job? = null

    init {
        setColorSchemeResources(R.color.seed)
        setProgressViewOffset(true, 0, 120)
    }

    fun onPauseView(){
        isRefreshing = false
        clearAnimation()
    }

    fun onResumeView(loadingState : Boolean){
        isRefreshing = loadingState
    }

    fun isRefreshingDelayed(viewLifecycleOwner: LifecycleOwner, value: Boolean, millis: Long = 300){
        loadingAnimationTask?.cancel()
        loadingAnimationTask = viewLifecycleOwner.lifecycleScope.launch {
            if(value)
                delay(millis)
            isRefreshing = value
        }
    }
}