package com.example.siriusproject

import android.app.Activity
import android.os.Bundle
import android.hardware.Camera;
import android.widget.FrameLayout
import java.io.IOException

class CameraActivity : Activity() {

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        mCamera = getCameraInstance()

        mPreview = mCamera?.let {
            CameraPreview(this, it)
        }
        mPreview?.also {
            val preview: FrameLayout = findViewById(R.id.camera_preview)
            preview.addView(it)
        }
    }
    private fun getCameraInstance(): Camera? {
        return try {
            Camera.open()
        } catch (e: IOException) {
            null
        }
    }
}
