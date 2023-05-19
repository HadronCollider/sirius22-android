package com.example.siriusproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import android.graphics.Bitmap

object Utils {

    val REQUEST_CODE_PERMISSIONS = 10

    fun allPermissionsGranted(context: Context) = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    val REQUIRED_PERMISSIONS = mutableListOf(
        Manifest.permission.CAMERA
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }.toTypedArray()


    fun compressImage(bitmap: Bitmap): Bitmap {
        var height = bitmap.height
        var width = bitmap.width
        val smallerIn = 5           // во столько раз маленткое изобарежние меньше основного
        height /= smallerIn
        width /= smallerIn
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}