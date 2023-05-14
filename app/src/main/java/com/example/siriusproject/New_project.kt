package com.example.siriusproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.siriusproject.databinding.ActivityNewProjectBinding

class NewProject : AppCompatActivity() {

    private lateinit var viewBinding: ActivityNewProjectBinding
    private val START_FOR_RESULT = 1

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityNewProjectBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        val projectActivity = Intent(this, ProjectActivity::class.java)
        viewBinding.createProject.setOnClickListener {
            if (viewBinding.input.text.toString() == "") {
                val toast =
                    Toast.makeText(applicationContext, R.string.wrong_name, Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }
            projectActivity.putExtra(
                this.getString(R.string.name_type),
                viewBinding.input.text.toString()
            )
            projectActivity.putExtra(
                this.getString(R.string.type_type),
                this.getString(R.string.new_project_made)
            )
            startActivityForResult(projectActivity, START_FOR_RESULT)
        }
        viewBinding.topAppBar.setNavigationOnClickListener {
            this.finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        this.finish()
    }
}

