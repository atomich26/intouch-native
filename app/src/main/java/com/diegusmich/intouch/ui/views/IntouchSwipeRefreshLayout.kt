package com.diegusmich.intouch.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.diegusmich.intouch.R

class IntouchSwipeRefreshLayout(ctx : Context, attrs : AttributeSet) : SwipeRefreshLayout(ctx, attrs){

    init {
        setColorSchemeResources(R.color.seed)
        setProgressViewOffset(true, 0, 120)
    }
}