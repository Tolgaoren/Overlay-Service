package com.example.overlayservice.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.overlayservice.R
import com.example.overlayservice.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            openButton.setOnClickListener {

            }
            closeButton.setOnClickListener {

            }
        }

    }


}