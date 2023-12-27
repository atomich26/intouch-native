package com.diegusmich.intouch.ui.activities

import android.os.Bundle
import androidx.fragment.app.commit
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivityUserBinding
import com.diegusmich.intouch.ui.fragments.ProfileFragment

class UserActivity : BaseActivity() {

    private var _binding: ActivityUserBinding? = null
    private val binding get() = _binding!!

    private var userIdArg: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserBinding.inflate(layoutInflater)

        setContentView(binding.root)

        userIdArg = intent.extras?.getString(USER_ARG)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.profileLayoutHost, ProfileFragment.newInstance(userIdArg))
            }
        }
    }

    override fun lifecycleStateObserve() {
        return
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val USER_ARG = "userId"
    }
}