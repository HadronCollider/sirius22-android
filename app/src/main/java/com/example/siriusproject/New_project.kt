package com.example.siriusproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar

class NewProject : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_project)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.toolbar_activity_new_project)

        radioGroup = findViewById(R.id.radio_buttons)
        var qualityButton = RadioButton(this)
        qualityButton.setText(R.string.low_quality)
        radioGroup.addView(qualityButton)

        qualityButton = RadioButton(this)
        qualityButton.setText(R.string.middle_quality)
        radioGroup.addView(qualityButton)
        qualityButton = RadioButton(this)
        qualityButton.setText(R.string.high_quality)
        radioGroup.addView(qualityButton)

        val projectName = findViewById<EditText>(R.id.input)

        val projectActivity = Intent(this, ProjectActivity::class.java)

        val createProject = findViewById<Button>(R.id.create_project)
        createProject.setOnClickListener {
            if (projectName.text.toString() == "") {
                val toast =
                    Toast.makeText(applicationContext, R.string.wrong_name, Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }
            val chosenButton = radioGroup.checkedRadioButtonId
            if (chosenButton == -1) {
                val toast =
                    Toast.makeText(applicationContext, R.string.wrong_quality, Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }
            //следующие две строки по-сути ничего не делают, это было исправлено в другой ветке.
            savedInstanceState?.putString(this.getString(R.string.quality_type), radioGroup.checkedRadioButtonId.toString())
            savedInstanceState?.putString(this.getString(R.string.name_type), projectName.text.toString())
            savedInstanceState?.putString(this.getString(R.string.type_type), this.getString(R.string.new_project_made))
            projectActivity.putExtra(this.getString(R.string.type_type), this.getString(R.string.new_project_made))
            startActivity(projectActivity)
        }
    }
}

