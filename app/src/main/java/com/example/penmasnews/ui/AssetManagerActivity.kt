package com.example.penmasnews.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.penmasnews.R
import com.example.penmasnews.model.EventStorage

class AssetManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asset_manager)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAssets)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val events = EventStorage.loadEvents(this)
        val images = events.mapNotNull { if (it.imagePath.isNotBlank()) it.imagePath else null }
        val adapter = AssetGridAdapter(images)
        recyclerView.adapter = adapter
    }
}
