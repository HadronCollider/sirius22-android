package com.example.siriusproject


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.siriusproject.data.ReadProjectData
import java.util.Calendar
import java.util.Date

class ProjectActivity : AppCompatActivity() {

    private var id = 1
    private var name = ""
    private var quality: Byte = 0
    private lateinit var date: Date
    private lateinit var allData:ReadProjectData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.toolbar_activity_project)
        val arguments = intent.extras
        allData = ReadProjectData(this.filesDir)
        if (arguments?.getBoolean(R.string.type_type.toString()) == false) {
            name = arguments.getString(R.string.name_type.toString()).toString()
            quality = arguments.getByte(R.string.quality_type.toString())
            date = Calendar.getInstance().time
            if (allData.allProjectsData.isNotEmpty()) {
                id = allData.allProjectsData[(allData.allProjectsData.size - 1)].id + 1
            }
        }
    }

    override fun onPause() {
        super.onPause()
        date = Calendar.getInstance().time
        allData.writeAllDataToFile()
    }

    override fun onStop() {
        super.onStop()
        date = Calendar.getInstance().time
        allData.writeAllDataToFile()
    }
    override fun onDestroy() {
        super.onDestroy()
        date = Calendar.getInstance().time
        allData.writeAllDataToFile()
    }
}