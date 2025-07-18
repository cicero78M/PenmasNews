package com.example.penmasnews.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import android.graphics.BitmapFactory

class AssetGridAdapter(
    private val items: List<String>
) : RecyclerView.Adapter<AssetGridAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageAsset)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_asset_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path = items[position]
        if (path.startsWith("http")) {
            Thread {
                try {
                    val bmp = BitmapFactory.decodeStream(java.net.URL(path).openStream())
                    holder.image.post { holder.image.setImageBitmap(bmp) }
                } catch (_: Exception) {
                    holder.image.post { holder.image.setImageResource(android.R.drawable.ic_menu_gallery) }
                }
            }.start()
        } else {
            val bitmap = BitmapFactory.decodeFile(path)
            if (bitmap != null) {
                holder.image.setImageBitmap(bitmap)
            } else {
                holder.image.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
