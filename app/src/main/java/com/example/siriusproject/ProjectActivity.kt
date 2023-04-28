package com.example.siriusproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.example.siriusproject.boofcv.DemoMain
import com.example.siriusproject.data.*
import com.example.siriusproject.databinding.ActivityProjectBinding
import com.example.siriusproject.databinding.ToolbarActivityProjectBinding
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

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
        toolbarBinding.iconsNavig.setOnClickListener {
            // TODO: Добавить проверку на доступность камеры
            val intent = Intent(this, DemoMain::class.java)
            intent.putExtra("project_path", dirOfThisProject)
            startActivity(intent)
        }
        val addImage = findViewById<Button>(R.id.add_images)
        addImage.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, galleryRequest)
        }

        val cameraActivity = Intent(this, CameraActivity::class.java)
        viewBinding.openCamera.setOnClickListener {
            cameraActivity.putExtra(this.getString(R.string.path_to_dir), dirOfThisProject)
            startActivity(cameraActivity)
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

        adapter = ImageAdapter(object : ImageActionListener {
            override fun onClicked(image: Uri) {
                val imageActivity = Intent(this@ProjectActivity, ImageActivity::class.java)
                imageActivity.putExtra(
                    this@ProjectActivity.getString(R.string.image_data), image.path
                )
                startActivity(imageActivity)
            }

            override fun onRemove(image: Uri) {
                val file = image.path?.let { File(it) }
                val result = file?.delete()
                if (result == true) {
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

    override fun onRestart() {
        super.onRestart()
        getAllImages()
        adapter.data = allImages
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var bitmap: Bitmap?

        when (requestCode) {
            galleryRequest -> {
                if (resultCode == RESULT_OK) {
                    val selectedImage: Uri? = data?.data
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                        bitmap = rotateImage(bitmap, selectedImage)
                        val imageName = Calendar.getInstance().timeInMillis.toString() + ".jpeg"
                        val file = File(
                            dirOfThisProject, imageName
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
        var firstPosition = true
        allImages.clear()
        File("/$dirOfThisProject").walk().forEach {
            if (!firstPosition) {
                if (it.path.toString().endsWith(".jpeg") || it.path.endsWith(".jpg") || it.path.endsWith(".png")) {
                    allImages.add(it.toUri())
                }
            }
            firstPosition = false
        }
    }

    //поворот изображения
    private fun rotateImage(bitmap: Bitmap, selectedImage: Uri?): Bitmap {
        val imagePfd = selectedImage?.let { contentResolver.openFileDescriptor(it, "r") }
        val exif = imagePfd?.let { ExifInterface(it.fileDescriptor) }
        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
        )
        val rotate = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            else -> 0
        }
        val matrix = Matrix()
        matrix.postRotate(rotate.toFloat())
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )

    }
}