package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diegusmich.intouch.databinding.EventListFragmentLayoutBinding
import com.diegusmich.intouch.ui.adapters.EventsListAdapter
import com.diegusmich.intouch.ui.viewmodels.EventCategoryViewModel
import com.diegusmich.intouch.ui.viewmodels.EventCreatedViewModel
import com.diegusmich.intouch.ui.viewmodels.EventJoinedViewModel
import com.diegusmich.intouch.ui.viewmodels.EventsFilterListViewModel

private const val FILTER_ARG: String = "filterType"
private const val ID_ARG: String = "id"

class EventListFragment : Fragment() {

    private lateinit var viewModel: EventsFilterListViewModel
    private var filterArg: String? = null
    private var idArg: String? = null

    private var _binding: EventListFragmentLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idArg = arguments?.getString(ID_ARG)
        filterArg = arguments?.getString(FILTER_ARG)

        val viewModelClazz = when (filterArg) {
            EventsFilterType.JOINED.toString() -> EventJoinedViewModel::class.java
            EventsFilterType.CREATED.toString() -> EventCreatedViewModel::class.java
            EventsFilterType.BY_CATEGORIES.toString() -> EventCategoryViewModel::class.java
            else -> throw IllegalArgumentException("Tipo di filtro non valido")
        }

        viewModel = ViewModelProvider(this)[viewModelClazz]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EventListFragmentLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.eventListSwipeRefreshLayout.setOnRefreshListener {
            viewModel.onLoadEvents(idArg, true)
        }

        binding.eventListRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.eventListRecyclerView.adapter = EventsListAdapter(mutableListOf())
        observeData()
        viewModel.onLoadEvents(idArg)
    }

    private fun observeData() {
        viewModel.events.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty())
                (binding.eventListRecyclerView.adapter as EventsListAdapter).replace(it)
        }

        viewModel.LOADING.observe(viewLifecycleOwner) {
            binding.eventListSwipeRefreshLayout.isRefreshingDelayed(this, it)
        }

        viewModel.ERROR.observe(viewLifecycleOwner) {
            if (it != null)
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String?, filterType: EventsFilterType): EventListFragment {
            return EventListFragment().apply {
                arguments = bundleOf(ID_ARG to id, FILTER_ARG to filterType.toString())
            }
        }
    }

    sealed interface EventsFilterType {
        data object JOINED : EventsFilterType
        data object CREATED : EventsFilterType
        data object BY_CATEGORIES : EventsFilterType
    }
}