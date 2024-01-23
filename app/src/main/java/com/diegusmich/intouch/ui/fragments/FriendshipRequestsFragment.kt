package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.FriendshipRequest
import com.diegusmich.intouch.databinding.FragmentFriendshipRequestsBinding
import com.diegusmich.intouch.ui.activities.MainActivity
import com.diegusmich.intouch.ui.adapters.FriendshipRequestAdapter
import com.diegusmich.intouch.ui.adapters.MutableAdapter
import com.diegusmich.intouch.ui.viewmodels.FriendshipRequestViewModel
import com.google.android.material.appbar.MaterialToolbar

class FriendshipRequestsFragment : Fragment() {

    private var _binding : FragmentFriendshipRequestsBinding? = null
    private val binding get() = _binding!!

    private var viewModel : FriendshipRequestViewModel? = null
    private lateinit var toolbar : MaterialToolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentFriendshipRequestsBinding.inflate(layoutInflater, container, false)
        toolbar = binding.appBarLayout.materialToolbar

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (requireActivity() as MainActivity).friendshipRequestsViewModel

        toolbar.title = getString(R.string.friendship_requests_title)

        binding.friendshipRequestListView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            viewModel?.friendshipRequests?.value?.let{
                adapter = FriendshipRequestAdapter(it)
            }
        }

        binding.swipeRefreshLayout.isEnabled = false
        observeData()
    }

    private fun observeData(){
        viewModel?.let{ vm ->
            vm.friendshipRequests.observe(viewLifecycleOwner){
                if(it != null){
                    (binding.friendshipRequestListView.adapter as FriendshipRequestAdapter).replace(it)
                    (requireActivity() as MainActivity).addFriendshipIconBadge(it.size)
                }
            }

            vm.ERROR.observe(viewLifecycleOwner){
                it?.let {
                    Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT).show()
                }
            }

            vm.LOADING.observe(viewLifecycleOwner){
                binding.swipeRefreshLayout.isRefreshing = it
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.swipeRefreshLayout.onResumeView(viewModel?.LOADING?.value ?: false)
    }

    override fun onPause() {
        super.onPause()
        binding.swipeRefreshLayout.onPauseView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}