package com.example.siriusproject.data

import android.net.Uri

interface ActionListener {
    fun onClicked(project: ProjectData)
    fun onRemove(project: ProjectData)
    fun onClicked(image: Uri)
    fun onRemove(image: Uri)
}