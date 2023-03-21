package com.example.siriusproject.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.siriusproject.databinding.OneProjectBinding

class ProjectAdapter(private val projectActionListener: ActionListener) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>(), View.OnClickListener {
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
            holder.binding.project.setOnClickListener {
                projectActionListener.onClicked(project)
            }
            holder.binding.deleteProject.setOnClickListener {
                projectActionListener.onRemove(project)
            }
        }
    }

    override fun onClick(view: View) {
        val project: ProjectData = view.tag as ProjectData
        projectActionListener.onClicked(project)
    }


    class ProjectViewHolder(val binding: OneProjectBinding) : RecyclerView.ViewHolder(binding.root)
}