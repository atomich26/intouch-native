package com.diegusmich.intouch.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivityUserBinding
import com.diegusmich.intouch.ui.fragments.ProfileFragment

class UserActivity : AppCompatActivity() {

    private var userIdArg: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        userIdArg = intent.extras?.getString(USER_ARG)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.profileLayoutHost, ProfileFragment.newInstance(userIdArg, true))
            }
        }
    }

    companion object {
        const val USER_ARG = "userId"
    }
}