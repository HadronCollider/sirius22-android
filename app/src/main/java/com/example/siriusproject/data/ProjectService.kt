package com.example.siriusproject.data




class ProjectService() {

    private lateinit var projects: MutableList<ProjectData>
    private var listeners = mutableListOf<ProjectListener>()

    fun addListener(listener: ProjectListener) {
        listeners.add(listener)
        listener.invoke(projects)
    }

    fun removeListener(listener: ProjectListener) {
        listeners.remove(listener)
        listener.invoke(projects)
    }

    private fun notifyChanges() = listeners.forEach { it.invoke(projects) }
}