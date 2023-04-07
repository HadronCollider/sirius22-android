package com.example.siriusproject.data

import android.net.Uri

interface ImageActionListener {
    fun onClicked(image: Uri)
    fun onRemove(image: Uri)
}