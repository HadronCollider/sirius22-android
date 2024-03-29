package com.example.siriusproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.example.siriusproject.boofcv.MultiViewStereoActivity
import com.example.siriusproject.Constants.qualityOfImages
import com.example.siriusproject.data.*
import com.example.siriusproject.databinding.ActivityProjectBinding
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class ProjectActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityProjectBinding
    private var data = ProjectData(1, "", Calendar.getInstance().time)
    private lateinit var allData: ReadProjectData
    private lateinit var dirOfThisProject: String
    private lateinit var dirOfSmallImages: String
    private var galleryRequest = 1

    private var allImages: MutableList<Uri> = mutableListOf()
    private lateinit var adapter: ImageAdapter


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.topAppBar.setNavigationOnClickListener {
            this@ProjectActivity.finish()
        }

        viewBinding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.build_model -> {
                    if (Utils.allPermissionsGranted(this)) {
                        startBuildingActivity()
                    } else {
                        ActivityCompat.requestPermissions(
                            this, Utils.REQUIRED_PERMISSIONS, Utils.REQUEST_CODE_PERMISSIONS
                        )
                    }
                    true
                }
                R.id.settings -> {
                    Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        viewBinding.addImages.setOnClickListener {
            if (allImages.size >= Constants.MAX_COUNT_OF_IMAGES) {
                Toast.makeText(this, this.getString(R.string.count_of_images), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, galleryRequest)
        }

        val cameraActivity = Intent(this, CameraActivity::class.java)
        viewBinding.openCamera.setOnClickListener {
            cameraActivity.putExtra(this.getString(R.string.path_to_dir), dirOfThisProject)
            cameraActivity.putExtra(this.getString(R.string.now_count_of_images), allImages.size)
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
        viewBinding.topAppBar.title = data.name
        dirOfThisProject = this.filesDir.absolutePath + data.name + data.id + "/"
        dirOfSmallImages = dirOfThisProject + "img/"
        try {
            Files.createDirectory(Paths.get(dirOfThisProject))
            Files.createDirectory(Paths.get(dirOfSmallImages))
        } catch (e: IOException) {
            Log.d("files", "can't make a new directory")
        }
    }

    override fun onResume() {
        super.onResume()
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
                var file = image.path?.let { File(it) }
                var result = file?.delete()
                file = File(dirOfSmallImages + image.toFile().name)
                result = file.delete() && result == true
                if (result == true) {
                    allImages.remove(image)
                    adapter.deleteBimap(image.toFile().name)
                    adapter.data = allImages
                } else {
                    Toast.makeText(
                        this@ProjectActivity,
                        this@ProjectActivity.getString(R.string.delete_f),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }, dirOfThisProject)
        adapter.data = allImages
        viewBinding.imageList.adapter = adapter

    }

    private fun writeNewData(arguments: Bundle) {
        data.name = arguments.getString(this.getString(R.string.name_type)).toString()
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
                        var file = File(
                            dirOfThisProject, imageName
                        )
                        var os = BufferedOutputStream(FileOutputStream(file))
                        bitmap.compress(Bitmap.CompressFormat.JPEG, qualityOfImages, os)
                        os.close()
                        if (selectedImage != null) {
                            allImages.add(file.toUri())
                            adapter.data = allImages

                        }
                        file = File(dirOfSmallImages, imageName)
                        os = BufferedOutputStream(FileOutputStream(file))
                        bitmap = Utils.compressImage(bitmap)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, qualityOfImages, os)
                        os.close()


                        viewBinding.imageList.adapter = adapter
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun getAllImages() {
        allImages.clear()
        File(dirOfThisProject).walk().forEach {
            if ((it.path.toString()
                    .endsWith(".jpeg") || it.path.endsWith(".jpg") || it.path.endsWith(".png")) &&
                        checkThePositionOfFile(it.path.toString())
                    ) {
                allImages.add(it.toUri())
            }
        }
    }

    private fun checkThePositionOfFile(path: String): Boolean {
        if (path.length > dirOfThisProject.length) {
            path.substring(dirOfThisProject.length - 1, path.length).forEachIndexed { index, it ->
                if (it == '/' && index != 0)
                    return false
            }
        }
        return true
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Utils.REQUEST_CODE_PERMISSIONS) {
            if (Utils.allPermissionsGranted(this)) {
                startBuildingActivity()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun startBuildingActivity() {
        val intent = Intent(this, MultiViewStereoActivity::class.java)
        intent.putExtra("project_path", dirOfThisProject)
        startActivity(intent)
    }
}