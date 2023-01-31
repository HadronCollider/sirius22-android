package com.example.siriusproject


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.ActionBar

class ProjectActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.toolbar_activity_project)

        val openCameraButton = findViewById<Button>(R.id.open_camera)
        val cameraActivity = Intent(this, CameraActivity::class.java)
        openCameraButton.setOnClickListener {
            startActivity(cameraActivity)
        }
    }
}