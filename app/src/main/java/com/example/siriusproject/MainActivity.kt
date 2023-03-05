package com.example.siriusproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.siriusproject.data.*
import com.example.siriusproject.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var adapter: ProjectAdapter
    private val listener: ProjectListener = { adapter.data = it }


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
        val projectsData = ReadProjectData(this.filesDir)

        val manager = LinearLayoutManager(this)
        adapter = ProjectAdapter(object : ProjectActionListener {
            override fun onProjectGetId(project: ProjectData) {
                val projectActivity = Intent(this@MainActivity, ProjectActivity::class.java)
                startActivity(projectActivity)
            }
        })
        adapter.data = projectsData.allProjectsData
        adapter.addListener(listener)
        viewBinding.projectList.layoutManager = manager
        viewBinding.projectList.adapter = adapter
    }
}