package com.diegusmich.intouch.ui.views.form

import android.content.Context
import android.util.AttributeSet
import com.diegusmich.intouch.utils.TimeUtil
import java.util.Date

class IntouchDateInputLayout(ctx : Context, attrs: AttributeSet) : FormInputLayout<Date>(ctx, attrs) {

    override fun updateState(state: FormInputState<Date>) {
        editText?.text = null
        super.updateState(state)
    }

    override fun toText(data: Date?) : String{
        return if(data != null)
            TimeUtil.toLocaleString(data)
        else
            ""
    }
}