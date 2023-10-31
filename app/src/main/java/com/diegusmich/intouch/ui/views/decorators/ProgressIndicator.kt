package com.diegusmich.intouch.ui.views.decorators

import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.BaseProgressIndicatorSpec

fun <T : BaseProgressIndicatorSpec>BaseProgressIndicator<T>.visible(state : Boolean){
    if(state)
        this.show()
    else
        this.hide()
}