package com.example.siriusproject


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.siriusproject.databinding.ActivityProjectBinding

class ProjectActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityProjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.toolbar_activity_project)
    }
}