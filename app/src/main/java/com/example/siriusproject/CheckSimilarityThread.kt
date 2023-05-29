package com.example.siriusproject

import android.widget.ImageView
import androidx.camera.view.PreviewView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.example.siriusproject.data.ImageHash

class CheckSimilarityThread(
    private val previewCamera: PreviewView, private val lastImg: ImageView
) : Thread() {

    private val redBorder = 0.4
    private val yellowBorder = 0.6

    override fun run() {
        super.run()
        while (true) {
            if (previewCamera.bitmap == null && lastImg.drawable == null) {
                continue
            }
            val similarity = ImageHash.calcPercentSimilarImagesByHash(
                ImageHash.getPerceptualHash(previewCamera.bitmap!!),
                ImageHash.getPerceptualHash(lastImg.drawable.toBitmap())
            )

            if (similarity <= redBorder) {
                lastImg.background = R.drawable.red_border.toDrawable()
            } else if (similarity <= yellowBorder) {
                lastImg.background = R.drawable.yellow_border.toDrawable()
            } else {
                lastImg.background = R.drawable.green_border.toDrawable()
            }
        }
    }
}