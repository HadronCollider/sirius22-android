package com.example.siriusproject.data

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.siriusproject.databinding.OneImageBinding
import java.io.File

class ImageAdapter(private val imageActionListener: ActionListener) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>(), View.OnClickListener {

    var data: List<Uri> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = OneImageBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)

        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = data[position]


        with(holder.binding) {
            val image = File(imageUri.path)
            imageName.text = image.name
            val imageBitmap =  BitmapFactory.decodeFile(imageUri.path)
            holder.binding.imagePreview.setImageBitmap(imageBitmap)
            holder.binding.image.tag = imageUri
            holder.binding.imagePreview.tag = imageUri
            holder.binding.deleteProject.tag = imageUri
            holder.binding.imageName.tag = imageUri
            holder.binding.image.setOnClickListener {
                imageActionListener.onClicked(imageUri)
            }
            holder.binding.deleteProject.setOnClickListener {
                imageActionListener.onRemove(imageUri)
            }

        }
    }

    override fun onClick(view: View) {
        val image = view.tag as Uri
        imageActionListener.onClicked(image)
    }

    class ImageViewHolder(val binding: OneImageBinding) : RecyclerView.ViewHolder(binding.root)
}