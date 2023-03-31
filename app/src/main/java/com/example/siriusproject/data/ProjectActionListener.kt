package com.example.siriusproject.data


interface ProjectActionListener {
    fun onClicked(project: ProjectData)
    fun onRemove(project: ProjectData)
}