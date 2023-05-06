package com.example.siriusproject.data

import com.google.gson.annotations.SerializedName
import java.util.Date


data class ProjectData(

    @SerializedName("id") var id: Int,                              //id проекта

    @SerializedName("name") var name: String,                       // имя проекта, папка с содержимым проекта назвается также

    @SerializedName("date") var date: Date                          // дата последего изменения проекта
)

