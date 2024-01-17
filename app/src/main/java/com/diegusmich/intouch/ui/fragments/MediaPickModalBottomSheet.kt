package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider.getUriForFile
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.diegusmich.intouch.databinding.FragmentMediaPickModalBottomSheetBinding
import com.diegusmich.intouch.providers.CacheProvider
import com.diegusmich.intouch.providers.IntouchFileProvider
import com.diegusmich.intouch.ui.activities.MainActivity
import com.diegusmich.intouch.ui.viewmodels.EditProfileImageViewModel
import com.diegusmich.intouch.ui.views.decorators.visible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val IMAGE_PATH_ARG: String = "imagePath"

class MediaPickModalBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentMediaPickModalBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProfileImageViewModel by viewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        it?.let {
            viewModel.onLoadImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.onSetCurrentImgRef(it.getString(IMAGE_PATH_ARG))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentMediaPickModalBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.openGalleryButton.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.deleteImageButton.setOnClickListener {
            viewModel.onRemoveImage()
        }

        binding.openCameraButton.setOnClickListener {
            val tempFile = CacheProvider.newImageTempFile()
            val uri = getUriForFile(
                requireContext(),
                IntouchFileProvider.AUTHORITY,
                tempFile
            )

            (requireActivity() as MainActivity).addOnCameraPicturePickedCallback {
                if(it) {
                    tempFile.createNewFile()
                    viewModel.onLoadImage(uri)
                }
            }
            (requireActivity() as MainActivity).pickImageFromCamera.launch(uri)
        }

        observeData()
    }

    private fun observeData() {
        viewModel.LOADING.observe(viewLifecycleOwner) {
            binding.progressCircular.visible(it)
            binding.profileImagePreviewOverlay.visibility = if (it) View.VISIBLE else View.GONE
            binding.openCameraButton.isEnabled = !it
            binding.openGalleryButton.isEnabled = !it
            binding.deleteImageButton.isEnabled = !it
        }

        viewModel.currentImgRef.observe(viewLifecycleOwner) {
            binding.deleteImageButton.visibility = if(it != null) View.VISIBLE else View.GONE
            binding.profileImagePreview.load(it)
        }

        viewModel.ERROR.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.IMAGE_REMOVED.observe(viewLifecycleOwner) {
            if (it)
                binding.profileImagePreview.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "media_pick_modal"

        @JvmStatic
        fun newInstance(imagePath: String?) =
            MediaPickModalBottomSheet().apply {
                arguments = bundleOf(IMAGE_PATH_ARG to imagePath)
            }
    }
}