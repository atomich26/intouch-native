package com.diegusmich.intouch.ui.views.form

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.diegusmich.intouch.utils.TimeUtil
import java.util.Date

class IntouchDateInputLayout(ctx: Context, attrs: AttributeSet) :
    FormInputLayout<Date>(ctx, attrs) {

    override fun updateState(state: FormInputState<Date>) {
        super.updateState(state)
        state.inputValue?.let {
            editText?.setText(toText(it), TextView.BufferType.EDITABLE)
        }
    }

    override fun toText(date: Date?): String {
        return date?.let { TimeUtil.toLocaleString(date, TimeUtil.DAY_OF_YEAR) } ?: ""
    }
}