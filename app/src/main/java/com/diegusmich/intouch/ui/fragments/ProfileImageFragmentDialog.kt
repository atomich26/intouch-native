package com.diegusmich.intouch.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.diegusmich.intouch.databinding.ProfileImageFragmentDialogBinding
import com.diegusmich.intouch.service.CloudImageService
import com.diegusmich.intouch.ui.viewmodels.ProfileImageFragmentViewModel

private const val IMAGE_PATH_ARG: String = "imagePath"
private const val CAN_EDIT_ARG: String = "canEdit"

class ProfileImageFragmentDialog : DialogFragment() {

    private var _binding: ProfileImageFragmentDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel : ProfileImageFragmentViewModel by viewModels()

    private var imagePathArg: String? = null
    private var canEditArg: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePathArg = arguments?.getString(IMAGE_PATH_ARG)
        canEditArg = arguments?.getBoolean(CAN_EDIT_ARG, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagePathArg?.let {
            viewModel.loadImage(it)
        }

        binding.profileImageEditButtonsGroup.visibility =
            if (canEditArg == true) View.VISIBLE else View.GONE

        if(imagePathArg.isNullOrBlank())
            binding.profileImageEditButtonsGroup

        binding.deleteUserImage.setOnClickListener{
            binding.profileImage.clear()
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(imagePath: String, canEdit: Boolean) =
            ProfileImageFragmentDialog().apply {
                arguments = bundleOf(IMAGE_PATH_ARG to imagePath, CAN_EDIT_ARG to canEdit)
            }
    }
}