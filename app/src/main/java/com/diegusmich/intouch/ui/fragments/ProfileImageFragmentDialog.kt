package com.diegusmich.intouch.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.diegusmich.intouch.databinding.ProfileImageFragmentDialogBinding
import com.diegusmich.intouch.service.CloudImageService

private const val IMAGE_PATH_ARG: String = "imagePath"

class ProfileImageFragmentDialog : DialogFragment() {

    private var _binding : ProfileImageFragmentDialogBinding? = null
    private val binding get() = _binding!!

    private var imagePath : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePath = arguments?.getString(IMAGE_PATH_ARG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagePath?.let {
            binding.profileImage.load(CloudImageService.USERS.imageRef(it))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = ProfileImageFragmentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            window?.setDimAmount(0.8f)
        }
    }

    companion object{
        @JvmStatic
        fun newInstance(imagePath: String) =
            ProfileImageFragmentDialog().apply {
                arguments = Bundle().apply {
                    putString(IMAGE_PATH_ARG, imagePath)
                }
            }
    }
}