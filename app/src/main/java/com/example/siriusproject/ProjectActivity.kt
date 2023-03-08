package com.example.siriusproject


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.siriusproject.databinding.ActivityProjectBinding
import com.example.siriusproject.databinding.ToolbarActivityProjectBinding
import com.example.siriusproject.data.ProjectData
import com.example.siriusproject.data.ReadProjectData
import java.util.Calendar

class ProjectActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityProjectBinding
    private lateinit var toolbarBinding: ToolbarActivityProjectBinding
    private var data = ProjectData(1, "", 0, Calendar.getInstance().time)
    private lateinit var allData: ReadProjectData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityProjectBinding.inflate(layoutInflater)
        toolbarBinding = ToolbarActivityProjectBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_project)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.customView = toolbarBinding.root
        supportActionBar?.setCustomView(R.layout.toolbar_activity_project)
        val arguments = intent.extras
        allData = ReadProjectData(this.filesDir)
        if (arguments?.getString(this.getString(R.string.type_type)) == this.getString(R.string.new_project_made)) {
            writeNewData(arguments)
        } else {
            val id = arguments!!.getInt(R.string.id_type.toString())
            val returnData = allData.getData(id)
            if (returnData != null) {
                data = returnData
            } else {
                Toast.makeText(this, "Error! Can't find the project", Toast.LENGTH_SHORT).show()
                writeNewData(arguments)
            }
        }
    }

    private fun writeNewData(arguments: Bundle) {
        data.name = arguments.getString(R.string.name_type.toString()).toString()
        data.quality = arguments.getShort(R.string.quality_type.toString())
        data.id = allData.getLastId()
        allData.writeData(data)
    }

        toolbarBinding.backButton.setOnClickListener {
            this.finish()
        }
        val arguments = intent.extras
        val projectName = arguments?.getString("Name").toString()
        toolbarBinding.pageTitle.text = projectName
    override fun onPause() {
        super.onPause()
        data.date = Calendar.getInstance().time
        allData.writeAllDataToFile()
    }

    override fun onDestroy() {
        super.onDestroy()
        data.date = Calendar.getInstance().time
        allData.writeAllDataToFile()
    }

    override fun onStop() {
        super.onStop()
        data.date = Calendar.getInstance().time
        allData.writeAllDataToFile()
    }
}