package com.example.siriusproject.data


import android.util.Log
import com.google.gson.Gson
import java.io.*
import java.util.Date


class ReadProjectData(path: File) {
    private val fileName = "projects.json"
    private val pathToDirectory = path.absolutePath + "/"
    private lateinit var allProjectsData: MutableList<ProjectData>

    init {
        try {
            val fileR = File(pathToDirectory, fileName)
            if (!fileR.exists()) {
                fileR.createNewFile()
            }
            val fileRead = FileReader(fileR.path)
            val allText = fileRead.readText()
            allProjectsData = mutableListOf()
            if (allText.isNotEmpty()) {
                allProjectsData =
                    Gson().fromJson(allText, Array<ProjectData>::class.java).toMutableList()
                if (allProjectsData.isNotEmpty())
                    Log.d("json file", allProjectsData.toString())
                else
                    Log.d("json file", "file is empty")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getData(id: Int): ProjectData? {
        for (i in allProjectsData) {
            if (i.id == id) {
                return i
            }
        }
        return null
    }

    fun getLastId(): Int {
        return if (allProjectsData.isNotEmpty())
            allProjectsData.last().id + 1
        else 1
    }

    fun writeData(information: ProjectData) {
        allProjectsData.add(information)
    }

    fun writeData(id: Int, name: String, quality: Short, data: Date) {
        allProjectsData.add(ProjectData(id, name, quality, data))
    }

    fun writeAllDataToFile() {
        val gson = Gson()
        val fileWrite = FileWriter(pathToDirectory + fileName)
        val jsonData = gson.toJson(allProjectsData)
        fileWrite.write(jsonData)
        fileWrite.close()
    }
}