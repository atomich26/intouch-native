package com.diegusmich.intouch.ui.views.form

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout

abstract class FormInputLayout<T>(private val ctx : Context, attrs: AttributeSet) : TextInputLayout(ctx, attrs){

    protected abstract fun toText(data : T?) : String

    open fun updateState(state : FormInputState<T>){

        if(editText?.text == null || editText?.text.toString() == "")
            editText?.setText(toText(state.inputValue), TextView.BufferType.EDITABLE)

        if(state.error != null )
            this@FormInputLayout.error = ctx.getString(state.error)
        else
            this@FormInputLayout.error = null
    }

    data class FormInputState<T>(
        val inputValue : T? = null,
        val isValid : Boolean = false,
        val error : Int? = null
    )
}