package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentCommentsModalBottomBinding
import com.diegusmich.intouch.ui.viewmodels.CommentsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.SAVE_ALL
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val POST_ID_ARG = "postId"

class CommentsModalBottomFragment : BottomSheetDialogFragment()  {

    private var _binding : FragmentCommentsModalBottomBinding? = null
    private val binding get() = _binding!!

    private val viewModel : CommentsViewModel by viewModels()
    private var postId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getString(POST_ID_ARG)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsModalBottomBinding.inflate(layoutInflater, container,false)
        (dialog as BottomSheetDialog).behavior.let{
            it.isDraggable = true
            it.state = BottomSheetBehavior.STATE_EXPANDED
            it.saveFlags = SAVE_ALL
            it.isFitToContents = false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.commentTextInputLayout.requestFocus()
    }

    companion object{
        const val TAG = "post_comments"
        @JvmStatic
        fun newInstance(postId: String) : CommentsModalBottomFragment{
            return CommentsModalBottomFragment().apply {
                arguments = bundleOf(POST_ID_ARG to postId)
            }
        }
    }
}