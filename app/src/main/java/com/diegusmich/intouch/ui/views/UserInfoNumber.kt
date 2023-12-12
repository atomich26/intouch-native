package com.diegusmich.intouch.ui.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.diegusmich.intouch.R
import com.google.android.material.card.MaterialCardView

class  UserInfoNumber(ctx : Context, attrs: AttributeSet) : MaterialCardView(ctx, attrs) {

    private val displayText: String

    init{
        ctx.theme.obtainStyledAttributes(attrs, R.styleable.UserInfoNumber, 0, 0).apply {
            try {
                displayText = getString(R.styleable.UserInfoNumber_displayText).toString()
            } finally {
                recycle()
            }
        }
        inflate(ctx, R.layout.profile_info_numbers, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        layoutParams = (layoutParams as LinearLayout.LayoutParams).apply {
            height = LinearLayout.LayoutParams.MATCH_PARENT
            width = 0
            weight = 1.0f
        }

        isClickable = true
        strokeWidth = 0
        setBackgroundColor(Color.TRANSPARENT)
        findViewById<TextView>(R.id.userInfoName).text = displayText
    }

    fun setInfoValue(value: Int){
        findViewById<TextView>(R.id.userInfoValue).text = value.toString()
    }
}