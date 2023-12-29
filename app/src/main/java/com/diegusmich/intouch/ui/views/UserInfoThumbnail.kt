package com.diegusmich.intouch.ui.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.view.setPadding
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.service.CloudImageService
import com.google.android.material.card.MaterialCardView

class UserInfoThumbnail(ctx: Context, attrs: AttributeSet) : MaterialCardView(ctx, attrs) {

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
        findViewById<GlideImageView>(R.id.userThumbnail).load(CloudImageService.USERS.imageRef(userInfo.img))
        findViewById<TextView>(R.id.userUsernameText).text = context.getString(R.string.username_formatted, userInfo.username)
    }
}