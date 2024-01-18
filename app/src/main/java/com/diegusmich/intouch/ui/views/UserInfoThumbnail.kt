package com.diegusmich.intouch.ui.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.providers.CloudImageProvider
import com.google.android.material.card.MaterialCardView

class UserInfoThumbnail(ctx: Context, attrs: AttributeSet) : MaterialCardView(ctx, attrs) {

    val imageView get() = findViewById<GlideImageView>(R.id.userThumbnail)
    val usernameTextView get() = findViewById<TextView>(R.id.userUsernameText)

    init {
        inflate(ctx, R.layout.user_info_card, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isClickable = true
        strokeWidth = 0
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun setUserInfo(userInfo: User.Preview) {
        imageView.load(CloudImageProvider.USERS.imageRef(userInfo.img))
        usernameTextView.text = context.getString(R.string.username_formatted, userInfo.username)
    }
}