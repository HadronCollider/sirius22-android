package com.example.siriusproject


import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.core.net.toUri
import com.example.siriusproject.data.ActionListener
import com.example.siriusproject.data.ImageAdapter
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
    private lateinit var allData: ReadProjectData
    private lateinit var dirOfThisProject: String
    private var galleryRequest = 1
    private var allImages: MutableList<Uri> = mutableListOf()
    private lateinit var adapter: ImageAdapter

    private val qualityOfImages =
        90            // используется при сохранении изображения от 0 до 100

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityProjectBinding.inflate(layoutInflater)
        toolbarBinding = ToolbarActivityProjectBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
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
            val id = arguments!!.getInt(this.getString(R.string.id_type))
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
        } catch (e: IOException) {
            Log.d("files", "can't make a new directory")
        }
        getAllImages()

        adapter = ImageAdapter(object : ActionListener {
            override fun onClicked(image: Uri) {
                val imageActivity = Intent(this@ProjectActivity, ImageActivity::class.java)
                imageActivity.putExtra(this@ProjectActivity.getString(R.string.image_data), image.path)
                startActivity(imageActivity)
            }

            override fun onRemove(image: Uri) {
                val file = File(image.path)
                val result = file.delete()
                if (result) {
                    Toast.makeText(
                        this@ProjectActivity,
                        this@ProjectActivity.getString(R.string.delete_s),
                        Toast.LENGTH_SHORT
                    ).show()
                    allImages.remove(image)
                    adapter.data = allImages
                } else {
                    Toast.makeText(
                        this@ProjectActivity,
                        this@ProjectActivity.getString(R.string.delete_f),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            // данные функции используются для другого списка
            override fun onClicked(project: ProjectData) {}
            override fun onRemove(project: ProjectData) {}
        })

        adapter.data = allImages
        viewBinding.imageList.adapter = adapter
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

        when (requestCode) {
            galleryRequest -> {
                if (resultCode == RESULT_OK) {
                    val selectedImage: Uri? = data?.data
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                        val imageName = Calendar.getInstance().timeInMillis.toString() + ".jpeg"
                        val file = File(
                            dirOfThisProject,
                            imageName
                        )
                        val os = BufferedOutputStream(FileOutputStream(file))
                        bitmap.compress(Bitmap.CompressFormat.JPEG, qualityOfImages, os)
                        os.close()
                        if (selectedImage != null) {
                            allImages.add(file.toUri())
                            adapter.data = allImages

                        }
                        viewBinding.imageList.adapter = adapter
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun getAllImages() {
        File("/$dirOfThisProject").walk().forEach {
            allImages.add(it.toUri())
        }
        allImages.removeAt(0)
    }
}