package com.example.siriusproject

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.siriusproject.databinding.ActivityImageBinding
import com.example.siriusproject.databinding.ToolbarActivityImageBinding

class ImageActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityImageBinding
    private lateinit var toolbarBinding: ToolbarActivityImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityImageBinding.inflate(layoutInflater)
        toolbarBinding = ToolbarActivityImageBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        supportActionBar?.displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.customView = toolbarBinding.root
        toolbarBinding.backButton.setOnClickListener {
            this@ImageActivity.finish()
        }
        val arguments = intent.extras
        val imagePath: String? = arguments?.getString(this.getString(R.string.image_data))
        val imageBimap = BitmapFactory.decodeFile(imagePath)
        viewBinding.showImage.setImageBitmap(imageBimap)
        var imageName = ""
        if (imagePath != null) {
            imageName = getName(imagePath)
        }
        toolbarBinding.imageName.text = imageName
    }

    fun getName(path: String): String {
        var name = ""
        for (i in path.length - 1 downTo 0) if (path[i] != '/') {
            name = path[i] + name
        } else break
        return name
    }
}