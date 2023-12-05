package com.diegusmich.intouch.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.MenuProvider
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.Friendship
import com.diegusmich.intouch.databinding.ProfileLayoutBinding
import com.diegusmich.intouch.service.CloudImageService
import com.diegusmich.intouch.ui.viewmodels.ProfileViewModel
import com.google.android.material.appbar.MaterialToolbar

class UserActivity : BaseActivity() {

    private var _binding : ProfileLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar : MaterialToolbar

    private val viewModel: ProfileViewModel by viewModels()
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        userId = intent.extras?.getString(USER_ARG)
        super.onCreate(savedInstanceState)

        _binding = ProfileLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.appBarLayout.materialToolbar

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadProfile(userId)
        }

        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.profile_title)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        toolbar.navigationIcon =  AppCompatResources.getDrawable(this, R.drawable.baseline_arrow_back_24)
        toolbar.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_auth_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
               return false
            }

        },this)
    }


    override fun lifecycleStateObserve() {
        viewModel.loadProfile(userId)

        viewModel.profile.observe(this){
            if(it == null)
                return@observe

            toolbar.title = it.username
            binding.userImageProfile.load(CloudImageService.USERS.imageRef(it.img))
            binding.nameProfileLayout.text = it.name
            binding.biographyProfileLayout.text = it.biography
            binding.userInfoFriendship.setInfoValue(it.friends)
            binding.userInfoCreated.setInfoValue(it.created)
            binding.userInfoJoined.setInfoValue(it.joined)

            when (it.friendship.status) {
                is Friendship.Status.PENDING -> {
                    val isVisible = if (it.friendship.status.isActor) View.GONE else View.VISIBLE
                    binding.friendshipRequestBanner.visibility = isVisible
                }

                else ->{

                }
            }
        }

        viewModel.LOADING.observe(this){
            binding.swipeRefreshLayout.isRefreshing = it
        }

        viewModel.ERROR.observe(this){
            if (it != null)
                Toast.makeText(this, getString(it), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val USER_ARG = "userId"
    }
}