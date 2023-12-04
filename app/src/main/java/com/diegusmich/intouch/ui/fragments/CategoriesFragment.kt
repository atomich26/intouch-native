package com.diegusmich.intouch.ui.fragments

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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentCategoriesBinding
import com.diegusmich.intouch.ui.activities.SearchActivity
import com.diegusmich.intouch.ui.adapters.CategoriesGridAdapter
import com.diegusmich.intouch.ui.viewmodels.CategoriesViewModel
import com.google.android.material.appbar.MaterialToolbar

class CategoriesFragment : BaseFragment() {

    private var _binding : FragmentCategoriesBinding? = null
    val binding get() = _binding!!

    private lateinit var toolbar : MaterialToolbar

    private val viewModel : CategoriesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        viewModel.categories.observe(viewLifecycleOwner){
            if (it != null) {
                binding.categoriesGridView.adapter = CategoriesGridAdapter(it)
            }
        }

        viewModel.LOADING.observe(this){
            binding.swipeRefreshLayout.isRefreshing = it
        }

        viewModel.ERROR.observe(this){
            if (it != null)
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT)
                    .show()
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