package com.example.siriusproject.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.siriusproject.databinding.OneProjectBinding

typealias ProjectListener = (project: List<ProjectData>) -> Unit

class ProjectAdapter(private val projectActionListener: ProjectActionListener) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>(), View.OnClickListener {
    var data: List<ProjectData> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    var listeners = mutableListOf<ProjectListener>()

    override fun getItemCount(): Int = data.size

    fun addListener(listener: ProjectListener) {
        listeners.add(listener)
        listener.invoke(data)
    }

    fun removeListener(listener: ProjectListener) {
        listeners.remove(listener)
        listener.invoke(data)
    }

    private fun notifyChanges() = listeners.forEach { it.invoke(data) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = OneProjectBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)

        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = data[position]

        with(holder.binding) {
            projectName.text = project.name
            dateLastChanged.text = project.date.toString()
            holder.binding.projectName.tag = project
            holder.binding.dateLastChanged.tag = project
            holder.binding.ellipse.tag = project
        }
    }

    override fun onClick(view: View) {
        val project: ProjectData = view.tag as ProjectData

        projectActionListener.onProjectGetId(project)
    }


    class ProjectViewHolder(val binding: OneProjectBinding) : RecyclerView.ViewHolder(binding.root)
}