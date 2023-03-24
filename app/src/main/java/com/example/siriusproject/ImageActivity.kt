package com.example.siriusproject

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.siriusproject.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val arguments = intent.extras
        val imagePath: String? = arguments?.getString(this.getString(R.string.image_data))
        val imageBimap = BitmapFactory.decodeFile(imagePath)
        viewBinding.showImage.setImageBitmap(imageBimap)
    }
}