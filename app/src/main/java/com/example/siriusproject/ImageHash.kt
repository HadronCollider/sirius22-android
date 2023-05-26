package com.example.siriusproject.data

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType.CV_64FC1
import org.opencv.core.CvType.CV_8UC1
import org.opencv.core.Mat
import org.opencv.core.Range
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.math.BigInteger

object ImageHash {
    // Хеш изображения через средний пиксель
    fun getAverageHash(bitmap: Bitmap): String {
        val preferredSquareSize = 32.0 // Константа может варьироваться в зависимости от устройства
        val srcMat = Mat()
        Utils.bitmapToMat(bitmap, srcMat)
        val distMat = Mat()
        Imgproc.resize(srcMat, distMat, Size(preferredSquareSize, preferredSquareSize))
        Imgproc.cvtColor(distMat, distMat, Imgproc.COLOR_RGB2GRAY)
        return hexHashByAveragePixel(distMat)
    }

    // Хеш изображения с помощью перцептивного алгоритма
    // Более робастый
    fun getPerceptualHash(bitmap: Bitmap, highFreqFactor: Int = 4): String {
        val preferredSquareSize = 32.0 // Константа может варьироваться в зависимости от устройства
        val srcMat = Mat()
        Utils.bitmapToMat(bitmap, srcMat)
        val distMat = Mat()
        Imgproc.resize(
            srcMat, distMat, Size(
                preferredSquareSize * highFreqFactor, preferredSquareSize * highFreqFactor
            )
        )
        Imgproc.cvtColor(distMat, distMat, Imgproc.COLOR_RGB2GRAY)
        val tempMat = Mat()
        distMat.convertTo(tempMat, CV_64FC1)
        Core.dct(tempMat, tempMat)
        tempMat.convertTo(distMat, CV_8UC1)
        val matForDct = distMat.submat(
            Range(0, preferredSquareSize.toInt()), Range(1, preferredSquareSize.toInt() + 1)
        )
        return hexHashByAveragePixel(matForDct)
    }

    fun calcPercentSimilarImagesByHash(imgHexHash1: String, imgHexHash2: String): Float {
        if (imgHexHash1.length != imgHexHash2.length) return -1f
        var cntSameSymbols = 0
        for (i in imgHexHash1.indices) {
            if (imgHexHash1[i] == imgHexHash2[i]) cntSameSymbols++
        }
        return cntSameSymbols.toFloat() / imgHexHash1.length
    }

    private fun hexHashByAveragePixel(distMat: Mat): String {
        val averagePixel = averagePixelInMat(distMat)
        val bitsString = StringBuilder("0".repeat(distMat.width() * distMat.height()))
        var indexOfStr = 0
        for (x in 0 until distMat.width()) {
            for (y in 0 until distMat.height()) {
                bitsString[indexOfStr++] = if (distMat.get(y, x)[0] >= averagePixel) '1'
                else '0'
            }
        }
        return BigInteger(bitsString.toString(), 2).toString(16)
            .padStart(distMat.width() * distMat.height() / 4, '0')
    }

    private fun averagePixelInMat(distMat: Mat): Double {
        var sumOfPixels = 0
        for (x in 0 until distMat.width()) {
            for (y in 0 until distMat.height()) {
                sumOfPixels += distMat.get(y, x)[0].toInt()
            }
        }
        return sumOfPixels / (distMat.height() * distMat.width()).toDouble()
    }

    // функция для подсчёта отличия 2 изображений
    fun hammingDistance(firstString: String, secondString: String): Int {
        var count = 0
        for (i in 0 until Math.min(firstString.length, secondString.length)) {
            if (firstString[i] != secondString[i]) {
                count++
            }
        }
        return count
    }
}