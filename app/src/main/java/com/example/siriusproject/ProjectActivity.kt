package com.example.siriusproject


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import com.example.siriusproject.databinding.ActivityProjectBinding
import com.example.siriusproject.databinding.ToolbarActivityProjectBinding
import com.example.siriusproject.data.ProjectData
import com.example.siriusproject.data.ReadProjectData
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Calendar

class ProjectActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityProjectBinding
    private lateinit var toolbarBinding: ToolbarActivityProjectBinding
    private var data = ProjectData(1, "", 0, Calendar.getInstance().time)
    private lateinit var allData:ReadProjectData
    private lateinit var dirOfThisProject: String
    private var galleryRequest = 1

    private val qualityOfImages = 90            // используется при сохранении изображения от 0 до 100

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityProjectBinding.inflate(layoutInflater)
        toolbarBinding = ToolbarActivityProjectBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_project)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.customView = toolbarBinding.root
        toolbarBinding.backButton.setOnClickListener {

            this@ProjectActivity.finish()
        }
        val addImage = findViewById<Button>(R.id.add_images)
        addImage.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, galleryRequest)
        }

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
        dirOfThisProject = this.filesDir.absolutePath + data.name + data.id + "/"
        try {
            Files.createDirectory(Paths.get(dirOfThisProject))
        } catch(e: IOException) {
            Log.d("files", "can't make a new directory")
        }
    }

    private fun writeNewData(arguments: Bundle) {
        data.name = arguments.getString(this.getString(R.string.name_type)).toString()
        data.quality = arguments.getShort(R.string.quality_type.toString())
        data.id = allData.getLastId()
        allData.writeData(data)
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
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var bitmap: Bitmap? = null
        val imageView:ImageView = findViewById(R.id.imagePreview)
        when(requestCode) {
            galleryRequest -> {
                if (resultCode == RESULT_OK) {
                    val selectedImage: Uri? = data?.data
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                        val file = File(dirOfThisProject, Calendar.getInstance().timeInMillis.toString() + ".jpeg")
                        val os = BufferedOutputStream(FileOutputStream(file))
                        bitmap.compress(Bitmap.CompressFormat.JPEG, qualityOfImages, os)
                        os.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    imageView.setImageBitmap(bitmap)
                }
            }
        }
    }
}