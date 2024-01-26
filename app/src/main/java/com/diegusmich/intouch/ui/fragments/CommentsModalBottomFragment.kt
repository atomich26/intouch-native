package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentCommentsModalBottomBinding
import com.diegusmich.intouch.ui.adapters.CommentsListAdapter
import com.diegusmich.intouch.ui.viewmodels.CommentsViewModel
import com.diegusmich.intouch.ui.views.decorators.visible
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
            it.state = BottomSheetBehavior.STATE_EXPANDED
            it.saveFlags = SAVE_ALL
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId?.let{
            viewModel.setPostId(it)
            viewModel.onLoadComments()
        }

        binding.commentTextEditLayout.doAfterTextChanged {
            it?.let{
                viewModel.onUpdateCommentText(it.toString())
            }
        }

        binding.commentTextInputLayout.setEndIconOnClickListener {
            viewModel.onAddNewComment()
        }

        binding.commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = CommentsListAdapter(mutableListOf())
        }

        observeData()
    }

    private fun observeData(){
        viewModel.LOADING.observe(viewLifecycleOwner){
            binding.pgLayout.progressBar.visible(it)
        }

        viewModel.comments.observe(viewLifecycleOwner){
            if(it != null && it.isNotEmpty())
                (binding.commentsRecyclerView.adapter as CommentsListAdapter).replace(it)
        }

        viewModel.commentText.observe(viewLifecycleOwner){
            binding.commentTextInputLayout.isEndIconCheckable = it.isValid
        }

        viewModel.COMMENT_ADDED.observe(viewLifecycleOwner){
            if(it){
                binding.commentTextEditLayout.text?.clear()
                Toast.makeText(requireContext(), getString(R.string.comment_added), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.ERROR.observe(viewLifecycleOwner){
            it?.let{
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT).show()
            }
        }
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