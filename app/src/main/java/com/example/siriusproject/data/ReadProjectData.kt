package com.example.siriusproject.data


import android.util.Log
import com.google.gson.Gson
import java.io.*


class ReadProjectData(path: File) {
    private val fileName = "projects.json"
    private val pathToDirectory = path.absolutePath + "/"
    lateinit var allProjectsData: List<ProjectData>

    init {
        try {
            val fileR = File(pathToDirectory, fileName)
            if (!fileR.exists()) {
                fileR.createNewFile()
            }
            val fileRead = FileReader(fileR.path)
            val allText = fileRead.readText()
            if (allText.isNotEmpty()) {
                allProjectsData =
                    Gson().fromJson(allText, Array<ProjectData>::class.java).toList()
                if (allProjectsData.isNotEmpty())
                    Log.d("json file", allProjectsData.toString())
                else
                    Log.d("json file", "file is empty")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun writeData(information: ProjectData) {
        allProjectsData = allProjectsData.plus(information)
    }

    @Override
    fun finalize() {
        val gson = Gson()
        val fileWrite = FileWriter(pathToDirectory + fileName)
        val jsonData = gson.toJson(allProjectsData)
        fileWrite.write(jsonData)
        fileWrite.close()
    }

}