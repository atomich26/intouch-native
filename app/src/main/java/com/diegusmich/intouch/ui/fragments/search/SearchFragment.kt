package com.diegusmich.intouch.ui.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentSearchBinding
import com.diegusmich.intouch.ui.adapters.CategoriesGridAdapter
import com.diegusmich.intouch.ui.fragments.SwipeRefreshFragment
import com.diegusmich.intouch.ui.state.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SearchFragment : SwipeRefreshFragment() {

    private var _binding : FragmentSearchBinding? = null
    val binding get() = _binding!!

    private val viewModel : SearchViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition(200, TimeUnit.MILLISECONDS)
        swipeRefreshLayout.isEnabled = false
        binding.categoriesGridView.layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

        if(viewModel.categories.value != null)
            binding.categoriesGridView.adapter = CategoriesGridAdapter(viewModel.categories.value!!)

        startPostponedEnterTransition()
        requireActivity().addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
              return true
            }

        }, viewLifecycleOwner)
    }

    override fun inflateRootView(inflater: LayoutInflater, container: ViewGroup?): ViewGroup {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun lifecycleStateObserve() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){

                viewModel.uiState.collect{
                    when(it){
                        is UiState.LOADING -> {
                            swipeRefreshLayout.isRefreshing = true
                        }

                        is UiState.LOADING_COMPLETED -> {
                            swipeRefreshLayout.isRefreshing = false
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}