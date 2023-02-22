package com.example.siriusproject


import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import com.example.siriusproject.data.ProjectData
import com.example.siriusproject.data.ReadProjectData
import java.util.Calendar
import java.util.Date

class ProjectActivity : AppCompatActivity() {

    private lateinit var data: ProjectData
    private lateinit var allData:ReadProjectData

    private var galleryRequest = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.toolbar_activity_project)
        val addImage = findViewById<Button>(R.id.add_images)
        addImage.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, galleryRequest)
        }
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var bitmap: Bitmap? = null
        var imageView:ImageView = findViewById(R.id.imagePreview)
        when(requestCode) {
            galleryRequest -> {
                if (resultCode == RESULT_OK) {
                    var selectedImage: Uri? = data?.data
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    imageView.setImageBitmap(bitmap)
                }
            }
        }
    }
}