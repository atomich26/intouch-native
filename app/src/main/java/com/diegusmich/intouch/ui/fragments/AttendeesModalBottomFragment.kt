package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentAttendeesModalBottomBinding
import com.diegusmich.intouch.ui.adapters.UsersListAdapter
import com.diegusmich.intouch.ui.viewmodels.EventViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AttendeesModalBottomFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAttendeesModalBottomBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttendeesModalBottomBinding.inflate(layoutInflater, container, false)
        (dialog as BottomSheetDialog).behavior.let {
            it.state = BottomSheetBehavior.STATE_EXPANDED
            it.saveFlags = BottomSheetBehavior.SAVE_ALL
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.attendees.value?.let {
            binding.attendeesModalTitle.text = getString(R.string.attendees_title_formatted, it.size)
        }

        binding.attendeesRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            viewModel.attendees.value?.let {
                adapter = UsersListAdapter(it)
            }
        }
    }
}