package com.diegusmich.intouch.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.FragmentIntroBinding

class IntroFragment : Fragment() {
    private var _binding : FragmentIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLoginNext.setOnClickListener{
            findNavController().navigate(R.id.action_introFragment_to_loginFragment)
        }

        binding.buttonCreateAccountNext.setOnClickListener{
            findNavController().navigate(R.id.action_introFragment_to_createAccountFragment)
        }
    }


    override fun onStart() {
        super.onStart()

        if(!binding.introBgAnimation.isAnimating)
            binding.introBgAnimation.playAnimation()
    }

    override fun onStop() {
        super.onStop()
        binding.introBgAnimation.pauseAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}