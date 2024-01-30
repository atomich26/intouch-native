package com.diegusmich.intouch.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentFeedBinding
import com.diegusmich.intouch.ui.activities.MainActivity
import com.diegusmich.intouch.ui.adapters.EventFeedAdapter
import com.diegusmich.intouch.ui.adapters.PostFeedAdapter
import com.diegusmich.intouch.ui.viewmodels.FeedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: MaterialToolbar

    private val viewModel: FeedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentFeedBinding.inflate(layoutInflater, container, false)
        toolbar = binding.appBarLayout.materialToolbar

        return binding.root
    }

    fun loadDataOnPermissionChecked(){
        viewModel.onLoadMainFeed()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = getString(R.string.feed_title)

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onLoadMainFeed(true)
        }

        binding.postsFeedListView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.postsFeedListView.adapter = PostFeedAdapter(mutableListOf())

        binding.eventsFeedListView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.eventsFeedListView.adapter = EventFeedAdapter(mutableListOf())

        val checkResult = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission_group.LOCATION)
        if(checkResult == PackageManager.PERMISSION_DENIED){
            (requireActivity() as MainActivity).requestLocationPermission{
                viewModel.onLoadMainFeed()
            }
        }else
            viewModel.onLoadMainFeed()
        observeData()
    }

    private fun observeData() {
        viewModel.LOADING.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = it
        }

        viewModel.postFeed.observe(viewLifecycleOwner) {
            it?.let {
                binding.postsFeedListView.apply {
                    visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
                    (adapter as PostFeedAdapter).replace(it)
                }
            }
        }

        viewModel.eventFeed.observe(viewLifecycleOwner) {
            it?.let {
                binding.eventsFeedListView.apply {
                    (adapter as EventFeedAdapter).replace(it)
                }
            }
        }

        viewModel.ERROR.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.GET_LOCATION_FAILED.observe(viewLifecycleOwner) {
            if (it && !viewModel.locationFailedMessageConsumed.value!!) {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle(resources.getString(R.string.info_dialog_title))
                    setMessage(resources.getString(R.string.location_failed_message))
                    setPositiveButton(resources.getString(R.string.info_dialog_confirm)) { dialog, _ ->
                        viewModel.consumeMessage()
                        dialog.dismiss()
                    }
                    setNegativeButton(getString(R.string.retry)) { dialog, _ ->
                        viewModel.onLoadMainFeed(true)
                        dialog.dismiss()
                    }
                }.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.swipeRefreshLayout.onResumeView(viewModel.LOADING.value!!)
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