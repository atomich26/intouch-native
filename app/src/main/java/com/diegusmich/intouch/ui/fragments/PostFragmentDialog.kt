package com.diegusmich.intouch.ui.fragments

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentPostBinding

class PostFragmentDialog : DialogFragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme() = R.style.Intouch_Theme_FullScreenDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appBarLayout.materialToolbar.apply {
            setBackgroundColor(Color.BLACK)
            navigationIcon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_close_24)
            setNavigationIconTint(Color.WHITE)
            setNavigationOnClickListener {
                this@PostFragmentDialog.dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}