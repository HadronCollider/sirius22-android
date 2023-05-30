package com.example.siriusproject

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.OrientationEventListener
import android.view.Surface.*
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toFile
import androidx.exifinterface.media.ExifInterface
import com.example.siriusproject.Constants.MAX_COUNT_OF_IMAGES
import com.example.siriusproject.Constants.qualityOfImages
import com.example.siriusproject.data.ImageHash
import com.example.siriusproject.databinding.ActivityCameraBinding
import kotlinx.coroutines.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var pathToDir: File
    private lateinit var allFilesDir: String
    private var nowCountOfImages = 0
    private var lastImg: Bitmap? = null
    private lateinit var checkSimilarityThread: CheckSimilarityThread

    private val checkSimilarity = CoroutineScope(Dispatchers.Default)

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) return

                val rotation = when (orientation) {
                    in 45 until 135 -> ROTATION_270
                    in 135 until 225 -> ROTATION_180
                    in 225 until 315 -> ROTATION_90
                    else -> ROTATION_0
                }
                imageCapture?.targetRotation = rotation

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.topAppBar.setNavigationOnClickListener {
            this.finish()
        }
        viewBinding.lastImg.imageAlpha = 100


        viewBinding.previewImg.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewBinding.lastImg.visibility = View.VISIBLE
                    checkSimilarity.launch {
                        if (isActive) {
                            while (true) {
                                val redBorder = 0.4
                                val yellowBorder = 0.6
                                val nowBitmap = withContext(Dispatchers.Main) {
                                    return@withContext viewBinding.viewFinder.bitmap
                                }
                                val lastBitmap = withContext(Dispatchers.Main) {
                                    viewBinding.lastImg.drawable.toBitmap()
                                }
                                if (nowBitmap != null) {
                                    val similarity = ImageHash.calcPercentSimilarImagesByHash(
                                        ImageHash.getPerceptualHash(nowBitmap),
                                        ImageHash.getPerceptualHash(lastBitmap)
                                    )
                                    var typeOfBorder: Drawable
                                    if (similarity <= redBorder) {
                                        typeOfBorder = R.drawable.red_border.toDrawable()
                                    } else if (similarity <= yellowBorder) {
                                        typeOfBorder = R.drawable.yellow_border.toDrawable()
                                    } else {
                                        typeOfBorder = R.drawable.green_border.toDrawable()
                                    }
                                    withContext(Dispatchers.Main) {
                                        viewBinding.lastImg.background = typeOfBorder
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    viewBinding.lastImg.visibility = View.GONE
                    checkSimilarity.cancel()
                }
            }
            return@OnTouchListener true

        })


        val arguments = intent.extras
        allFilesDir = arguments?.getString(this.getString(R.string.path_to_dir)).toString()
        nowCountOfImages = arguments?.getInt(this.getString(R.string.now_count_of_images))!!


        if (Utils.allPermissionsGranted(this)) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, Utils.REQUIRED_PERMISSIONS, Utils.REQUEST_CODE_PERMISSIONS
            )
        }

        pathToDir = File(this.filesDir.absolutePath.toString() + "/")
        viewBinding.imageCaptureButton.setOnClickListener {
            if (nowCountOfImages >= MAX_COUNT_OF_IMAGES) {
                Toast.makeText(
                    this, this.getString(R.string.count_of_images), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            takePhoto()
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
        checkSimilarityThread = CheckSimilarityThread(viewBinding.viewFinder, viewBinding.lastImg)
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat(
            FILENAME_FORMAT, Locale.US
        ).format(System.currentTimeMillis()) + ".jpeg"
        val fileToSave = File(allFilesDir, name)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            fileToSave
        ).build()

        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Log.d(TAG, msg)
                    output.savedUri?.let { rotateImage(it) }
                    nowCountOfImages++
                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-ss-SSS"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Utils.REQUEST_CODE_PERMISSIONS) {
            if (Utils.allPermissionsGranted(this)) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    fun rotateImage(image: Uri) {
        val exif = image.path?.let { ExifInterface(it) }
        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
        )
        val rotate = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            else -> 0
        }
        var bitmap = BitmapFactory.decodeFile(image.path)
        val matrix = Matrix()
        matrix.postRotate(rotate.toFloat())
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        if (lastImg != null) {
            Log.d(
                TAG, ImageHash.calcPercentSimilarImagesByHash(
                    ImageHash.getPerceptualHash(lastImg!!), ImageHash.getPerceptualHash(bitmap)
                ).toString()
            )
        }
        lastImg = bitmap
        viewBinding.lastImg.setImageBitmap(bitmap)
        var smallerBitmap = bitmap
        var file = image.path?.let { File(it) }
        var os = BufferedOutputStream(FileOutputStream(file))
        bitmap.compress(Bitmap.CompressFormat.JPEG, qualityOfImages, os)
        os.close()

        file = File(allFilesDir + "img/" + image.toFile().name)
        os = BufferedOutputStream(FileOutputStream(file))
        smallerBitmap = Utils.compressImage(bitmap)
        smallerBitmap.compress(Bitmap.CompressFormat.JPEG, qualityOfImages, os)
        os.close()
    }


    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }


}
