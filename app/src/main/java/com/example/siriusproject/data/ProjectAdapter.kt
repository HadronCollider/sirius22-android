package com.example.siriusproject.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.siriusproject.databinding.OneProjectBinding

class ProjectAdapter : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {
    var data: List<ProjectData> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = OneProjectBinding.inflate(inflater, parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = data[position]

        with(holder.binding) {
            projectName.text = project.name
            dateLastChanged.text = project.date.toString()
        }
    }

    class ProjectViewHolder(val binding: OneProjectBinding) : RecyclerView.ViewHolder(binding.root)
}