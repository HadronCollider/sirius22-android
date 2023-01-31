package com.example.siriusproject

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ImageActivity : AppCompatActivity() {

    private val keyToPath = "path"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val imageView: ImageView = findViewById(R.id.show_image)
        val path = savedInstanceState?.getString(keyToPath)
        imageView.setImageDrawable(Drawable.createFromPath(path))
    }
}