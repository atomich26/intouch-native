package com.diegusmich.intouch.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.UserListLayoutBinding
import com.diegusmich.intouch.ui.adapters.UsersListAdapter
import com.diegusmich.intouch.ui.viewmodels.UserFriendsActivityViewModel
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class UserFriendsActivity : AppCompatActivity() {

    private var _binding: UserListLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: MaterialToolbar

    private val viewModel: UserFriendsActivityViewModel by viewModels()
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        userId = intent.extras?.getString(USER_ARG)

        super.onCreate(savedInstanceState)
        _binding = UserListLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.appBarLayout.materialToolbar
        toolbar.title = getString(R.string.user_friends_activity)
        setSupportActionBar(toolbar)

        toolbar.navigationIcon =
            AppCompatResources.getDrawable(this, R.drawable.baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.userListSwipeRefreshLayout.setOnRefreshListener {
            viewModel.onLoadFriends(userId, true)
        }

        binding.userListRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.userListRecyclerView.adapter = UsersListAdapter(mutableListOf())

        observeData()
        viewModel.onLoadFriends(userId)
    }

    private fun observeData(){

        viewModel.userFriends.observe(this) {
            if (!it.isNullOrEmpty())
                (binding.userListRecyclerView.adapter as UsersListAdapter).replace(it)
        }

        viewModel.LOADING.observe(this) {
            binding.userListSwipeRefreshLayout.isRefreshingDelayed(this, it)
        }

        viewModel.ERROR.observe(this) {
            if (it != null)
                Toast.makeText(this, getString(it), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val USER_ARG = "userId"
    }
}