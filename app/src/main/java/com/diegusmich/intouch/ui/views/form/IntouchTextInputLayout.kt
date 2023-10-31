package com.diegusmich.intouch.ui.views.form

import android.content.Context
import android.util.AttributeSet

class IntouchTextInputLayout(ctx: Context, attrs: AttributeSet) : FormInputLayout<String>(ctx, attrs) {
    override fun toText(data: String?) = data ?: ""
}