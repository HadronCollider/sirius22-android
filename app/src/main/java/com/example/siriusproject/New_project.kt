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
import com.example.siriusproject.databinding.ActivityNewProjectBinding
import com.example.siriusproject.databinding.ToolbarActivityNewProjectBinding

class NewProject : AppCompatActivity() {

    private lateinit var viewBinding: ActivityNewProjectBinding
    private lateinit var toolbarBinding: ToolbarActivityNewProjectBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityNewProjectBinding.inflate(layoutInflater)
        toolbarBinding = ToolbarActivityNewProjectBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.customView = toolbarBinding.root

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
            projectActivity.putExtra(
                "Quality",
                viewBinding.radioButtons.checkedRadioButtonId.toString()
            )
            projectActivity.putExtra("Name", viewBinding.input.text.toString())
            projectActivity.putExtra(this.getString(R.string.type_type), this.getString(R.string.new_project_made))
            startActivity(projectActivity)
        }
        toolbarBinding.arrowBackNewProject.setOnClickListener {
            this.finish()
        }
    }
}

