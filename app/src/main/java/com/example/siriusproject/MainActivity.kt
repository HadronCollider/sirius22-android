package com.example.siriusproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.siriusproject.data.*
import com.example.siriusproject.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var adapter: ProjectAdapter
    private lateinit var projectsData: ReadProjectData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.toolbar_main)
        val newProjectActivity = Intent(this, NewProject::class.java)
        viewBinding.newProject.setOnClickListener {
            startActivity(newProjectActivity)
        }
        projectsData = ReadProjectData(this.filesDir)
        adapter = ProjectAdapter(object : ProjectActionListener {
            override fun onClicked(project: ProjectData) {
                val projectActivity = Intent(this@MainActivity, ProjectActivity::class.java)
                projectActivity.putExtra(this@MainActivity.getString(R.string.id_type), project.id)
                startActivity(projectActivity)
            }
            override fun onRemove(project: ProjectData) {
                val result = projectsData.removeProject(project.id)
                if (!result) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error, can't find the project",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                adapter.data = projectsData.getAllData()
            }
        })
        adapter.data = projectsData.getAllData()
        viewBinding.projectList.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        projectsData.writeAllDataToFile()
    }

    override fun onStop() {
        super.onStop()
        projectsData.writeAllDataToFile()
    }

    override fun onDestroy() {
        super.onDestroy()
        projectsData.writeAllDataToFile()
    }


    override fun onRestart() {
        super.onRestart()
        projectsData = ReadProjectData(this.filesDir)
        adapter.data = projectsData.getAllData()
    }
}