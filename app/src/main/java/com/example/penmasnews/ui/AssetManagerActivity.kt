package com.example.penmasnews.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R

class AssetManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asset_manager)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAssets)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        val images = List(9) { android.R.drawable.ic_menu_gallery }
        val adapter = AssetGridAdapter(images)
        recyclerView.adapter = adapter
    }
}
