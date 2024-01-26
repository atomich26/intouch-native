package com.diegusmich.intouch.ui.views.form

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout

abstract class FormInputLayout<T>(ctx : Context, attrs: AttributeSet) : TextInputLayout(ctx, attrs){

    protected abstract fun toText(data : T?) : String?

    open fun updateState(state : FormInputState<T>){
        state.inputValue?.let {
            editText?.text?.let{ editable ->
                val valueToText = toText(it)
                if(editable.toString() != valueToText)
                    editText?.setText(valueToText, TextView.BufferType.EDITABLE)
            }
        }
        if(state.error != null){
            this@FormInputLayout.error = context.getString(state.error)
        }
        else
            this@FormInputLayout.error = null
    }

    data class FormInputState<T>(
        val inputName : String? = null,
        val inputValue : T? = null,
        val isValid : Boolean = false,
        val error : Int? = null
    )
}