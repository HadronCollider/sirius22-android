package com.example.siriusproject.data

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toFile
import androidx.recyclerview.widget.RecyclerView
import com.example.siriusproject.databinding.OneImageBinding

class ImageAdapter(private val imageActionListener: ImageActionListener, private val pathToDir: String) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>(), View.OnClickListener {

    var data: List<Uri> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var imageBitmaps = LruCache<String, Bitmap>(100)

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
            imageName.text = imageUri.toFile().name
            var imageBitmap: Bitmap? = imageBitmaps.get(pathToDir + "img/" + imageUri.toFile().name)
            if (imageBitmap == null) {
                imageBitmap =  BitmapFactory.decodeFile(pathToDir + "img/" + imageUri.toFile().name)
                imageBitmaps.put(imageUri.toString(), imageBitmap)
            }

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