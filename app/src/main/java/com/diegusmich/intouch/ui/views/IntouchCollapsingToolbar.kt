package com.diegusmich.intouch.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.appbar.CollapsingToolbarLayout

class IntouchCollapsingToolbar(ctx: Context, attrs: AttributeSet) : CollapsingToolbarLayout(ctx, attrs) {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d("SCROLLS", oldh.toString())
    }
}