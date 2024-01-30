package com.diegusmich.intouch.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diegusmich.intouch.R
import com.diegusmich.intouch.databinding.ActivityAddPostBinding

class AddPostActivity : AppCompatActivity() {

    private var _binding : ActivityAddPostBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}