package com.example.siriusproject

import android.graphics.Bitmap

class Utils {
    companion object {
        fun compressImage(bitmap: Bitmap): Bitmap {
            var height = bitmap.height
            var width = bitmap.width
            val smallerIn = 5           // во столько раз маленткое изобарежние меньше основного
            height /= smallerIn
            width /= smallerIn
            return Bitmap.createScaledBitmap(bitmap, width, height, true)
        }
    }
}