package com.diegusmich.intouch.ui.views.form

import android.content.Context
import android.util.AttributeSet
import com.diegusmich.intouch.utils.TimeUtil
import java.util.Date

class IntouchTimeInputLayout(ctx: Context, attrs: AttributeSet) : FormInputLayout<Date>(ctx, attrs) {

    override fun toText(data: Date?): String? {
        return data?.let{
            TimeUtil.toLocaleString(it,  TimeUtil.HH_MM)
        }
    }

    override fun setError(state: FormInputState<Date>) {
        return
    }
}