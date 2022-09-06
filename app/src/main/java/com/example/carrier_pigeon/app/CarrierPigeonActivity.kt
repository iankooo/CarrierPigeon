package com.example.carrier_pigeon.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.carrier_pigeon.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarrierPigeonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
