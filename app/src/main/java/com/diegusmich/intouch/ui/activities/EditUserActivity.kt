package com.diegusmich.intouch.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import com.diegusmich.intouch.R
import com.diegusmich.intouch.ui.fragments.UpsertUserFragment

class EditUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.editProfileFormLayoutHost,  UpsertUserFragment().apply {
                    arguments = bundleOf(
                        EDIT_MODE_ARG to true
                    )
                })
            }
        }
    }

    companion object{
        const val EDIT_MODE_ARG: String = "edit_mode"
    }
}