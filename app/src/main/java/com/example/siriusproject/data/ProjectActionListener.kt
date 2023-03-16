package com.example.siriusproject.data

interface ProjectActionListener {
    fun onProjectClick(project: ProjectData)
    fun onRemoveProject(project: ProjectData)
}