package com.example.bankingnow.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bankingnow.R
import com.example.bankingnow.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}