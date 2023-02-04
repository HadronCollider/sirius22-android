package com.example.siriusproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.siriusproject.databinding.ActivityNewProjectBinding

class NewProject : AppCompatActivity() {

    private lateinit var viewBinding: ActivityNewProjectBinding


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityNewProjectBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.toolbar_activity_new_project)

        var qualityButton = RadioButton(this)
        qualityButton.setText(R.string.low_quality)
        viewBinding.radioButtons.addView(qualityButton)
        qualityButton = RadioButton(this)
        qualityButton.setText(R.string.middle_quality)
        viewBinding.radioButtons.addView(qualityButton)
        qualityButton = RadioButton(this)
        qualityButton.setText(R.string.high_quality)
        viewBinding.radioButtons.addView(qualityButton)


        val projectActivity = Intent(this, ProjectActivity::class.java)

        viewBinding.createProject.setOnClickListener {
            if (viewBinding.input.text.toString() == "") {
                val toast =
                    Toast.makeText(applicationContext, R.string.wrong_name, Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }
            val chosenButton = viewBinding.radioButtons.checkedRadioButtonId
            if (chosenButton == -1) {
                val toast =
                    Toast.makeText(applicationContext, R.string.wrong_quality, Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }

            savedInstanceState?.putString(
                "Quality",
                viewBinding.radioButtons.checkedRadioButtonId.toString()
            )
            savedInstanceState?.putString("Name", viewBinding.input.text.toString())
            startActivity(projectActivity)
        }
    }
}

