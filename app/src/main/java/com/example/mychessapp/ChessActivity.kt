package com.example.mychessapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mychessapp.databinding.ActivityChessBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChessActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityChessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityChessBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }


}