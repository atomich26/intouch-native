package com.diegusmich.intouch.ui.views.form

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class IntouchDropDownInputLayout(ctx: Context, attrs: AttributeSet) : FormInputLayout<Int>(ctx, attrs) {

    override fun updateState(state: FormInputState<Int>) {
        (editText as? MaterialAutoCompleteTextView)?.let {
            state.inputValue?.let{ value ->
                if(it.listSelection != value)
                    it.setText(it.adapter.getItem(value).toString(), false)
            }
        }
        if(state.error != null){
            this@IntouchDropDownInputLayout.error = context.getString(state.error)
        }
        else
            this@IntouchDropDownInputLayout.error = null
    }

    override fun toText(data: Int?) = null
}