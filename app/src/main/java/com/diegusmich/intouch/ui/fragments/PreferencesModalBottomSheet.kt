package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.PreferencesBottomSheetContentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class PreferencesModalBottomSheet : BottomSheetDialogFragment() {

    private var _binding : PreferencesBottomSheetContentBinding? = null
    val binding get() = _binding!!

    private var prefsArray : Array<String>? = null
    private var canEdit: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            prefsArray = arguments?.getStringArray(PREFS_ARRAY)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PreferencesBottomSheetContentBinding.inflate(inflater, container, false)
        prefsArray?.forEach {
            addPrefsChip(inflater, container, it)
        }
        return binding.root
    }

    private fun addPrefsChip(inflater: LayoutInflater, container:ViewGroup?, name: String){
        val chip = inflater.inflate(R.layout.user_chip_modal, container, false) as Chip
        chip.text = name
        binding.preferencesChipGroup.addView(chip)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val TAG = "PREF_MODAL_BOTTOM_SHEET"
        const val PREFS_ARRAY = "prefs"
        const val CAN_EDIT_ARG = "canEdit"
    }
}