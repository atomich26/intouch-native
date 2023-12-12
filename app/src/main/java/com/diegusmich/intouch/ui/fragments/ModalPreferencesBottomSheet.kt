package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.PreferencesBottomSheetContentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalPreferencesBottomSheet : BottomSheetDialogFragment() {

    private var _binding : PreferencesBottomSheetContentBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PreferencesBottomSheetContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object{
        const val TAG = "PREF_MODAL_BOTTOM_SHEET"
    }
}