package com.diegusmich.intouch.ui.views.form

import android.content.Context
import android.util.AttributeSet

class IntouchNumberInputLayout(ctx: Context, attrs: AttributeSet) : FormInputLayout<Int>(ctx, attrs) {

    override fun toText(data: Int?): String = data.toString()
}