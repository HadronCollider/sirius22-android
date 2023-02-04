package com.example.siriusproject

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.siriusproject.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityImageBinding

    private val keyToPath = "path"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val path = savedInstanceState?.getString(keyToPath)
        viewBinding.showImage.setImageDrawable(Drawable.createFromPath(path))
    }
}