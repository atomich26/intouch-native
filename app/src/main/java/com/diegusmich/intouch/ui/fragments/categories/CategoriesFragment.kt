package com.diegusmich.intouch.ui.fragments.categories

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentCategoriesBinding
import com.diegusmich.intouch.ui.activities.search.SearchActivity
import com.diegusmich.intouch.ui.adapters.CategoriesGridAdapter
import com.diegusmich.intouch.ui.fragments.BaseFragment
import com.diegusmich.intouch.ui.fragments.SwipeRefreshFragment
import com.diegusmich.intouch.ui.state.UiState
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class CategoriesFragment : BaseFragment() {

    private var _binding : FragmentCategoriesBinding? = null
    val binding get() = _binding!!

    private lateinit var toolbar : MaterialToolbar

    private val viewModel : CategoriesViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        toolbar = binding.appBarLayout.materialToolbar
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = getString(R.string.search_title)
        binding.categoriesGridView.layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadCategories()
        }

        toolbar.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.categories_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if(menuItem.itemId == R.id.startActivitySearch){
                    requireActivity().startActivity(Intent(requireContext(), SearchActivity::class.java))
                }

                return true
            }

        }, viewLifecycleOwner)
    }

    override fun lifecycleStateObserve() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED){

                launch {
                    viewModel.categories.collect {
                        if (it != null) {
                            binding.categoriesGridView.adapter = CategoriesGridAdapter(it)
                        }
                    }
                }

                viewModel.uiState.collect{
                    when(it){
                        is UiState.LOADING -> {
                           // swipeRefreshLayout.isRefreshing = true
                        }
                        is UiState.LOADING_COMPLETED -> {
                            //swipeRefreshLayout.isRefreshing = false
                        }
                        is UiState.ERROR -> {
                            Toast.makeText(requireContext(), getString(viewModel.errorMessage!!), Toast.LENGTH_SHORT).show()
                        }

                        else -> Unit
                    }

                    viewModel.consumeEvent()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}