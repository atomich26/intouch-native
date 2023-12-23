package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentCreateModalBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CreateModalBottomSheet : BottomSheetDialogFragment() {

    private var _binding : FragmentCreateModalBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateModalBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val TAG = "CREATE_MODAL_FRAGMENT"
    }
}