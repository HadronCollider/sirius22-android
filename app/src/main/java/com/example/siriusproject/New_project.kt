package com.example.siriusproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.ActionBar
import androidx.core.widget.doBeforeTextChanged

class New_project : AppCompatActivity() {

    lateinit var radioGroup:RadioGroup

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_project)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.toolbar_activity_new_project)

        radioGroup = findViewById<RadioGroup>(R.id.radio_buttons)
        var qualityButton = RadioButton(this)
        qualityButton.setText(R.string.low_quality)
        radioGroup.addView(qualityButton)

        qualityButton = RadioButton(this)
        qualityButton.setText(R.string.middle_quality)
        radioGroup.addView(qualityButton)
        qualityButton = RadioButton(this)
        qualityButton.setText(R.string.high_quality)
        radioGroup.addView(qualityButton)

        val projectActivity = Intent(this, project::class.java)

        val createProject = findViewById<Button>(R.id.create_project)
        createProject.setOnClickListener {
            var chosenButton = radioGroup.checkedRadioButtonId
            if (chosenButton == -1)
                return@setOnClickListener
            savedInstanceState?.putString("Quality", radioGroup.checkedRadioButtonId.toString())
            startActivity(projectActivity)
        }
    }
}

