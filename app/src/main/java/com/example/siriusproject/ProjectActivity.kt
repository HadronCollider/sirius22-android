package com.example.siriusproject


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.siriusproject.data.ProjectData
import com.example.siriusproject.data.ReadProjectData
import java.util.Calendar
import java.util.Date

class ProjectActivity : AppCompatActivity() {

    private lateinit var data: ProjectData
    private lateinit var allData:ReadProjectData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.toolbar_activity_project)
        val arguments = intent.extras
        allData = ReadProjectData(this.filesDir)
        data.id = 1
        if (arguments?.getBoolean(R.string.type_type.toString()) == false) {
            data.name = arguments.getString(R.string.name_type.toString()).toString()
            data.quality = arguments.getByte(R.string.quality_type.toString())
            data.date = Calendar.getInstance().time
            if (allData.allProjectsData.isNotEmpty()) {
                data.id = allData.allProjectsData[(allData.allProjectsData.size - 1)].id + 1
            }
        } else {
            data.id = arguments!!.getInt(R.string.id_type.toString())
            for (i in allData.allProjectsData) {
                if (i.id == data.id) {
                    data.name = i.name
                    data.quality = i.quality
                    data.date = i.date
                    break;
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        data.date = Calendar.getInstance().time
        allData.writeAllDataToFile()
    }

    override fun onStop() {
        super.onStop()
        data.date = Calendar.getInstance().time
        allData.writeAllDataToFile()
    }
    override fun onDestroy() {
        super.onDestroy()
        data.date = Calendar.getInstance().time
        allData.writeAllDataToFile()
    }
}