package com.example.siriusproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.siriusproject.databinding.ActivityModelPreviewBinding

class ModelPreview : AppCompatActivity() {

    private lateinit var viewBinding: ActivityModelPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityModelPreviewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }
}