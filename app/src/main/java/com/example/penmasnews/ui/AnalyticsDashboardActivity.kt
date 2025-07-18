package com.example.penmasnews.ui

import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AppCompatActivity
import com.example.penmasnews.R
import com.example.penmasnews.network.AnalyticsService
import com.example.penmasnews.ui.TrendingTopicAdapter
import com.example.penmasnews.ui.WordCloudView

class AnalyticsDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics_dashboard)

        val viewsText = findViewById<TextView>(R.id.textViews)
        val visitorsText = findViewById<TextView>(R.id.textVisitors)
        val bounceText = findViewById<TextView>(R.id.textBounce)
        val trendingList = findViewById<RecyclerView>(R.id.recyclerViewTrending)
        val wordCloud = findViewById<WordCloudView>(R.id.wordCloud)

        // Placeholder angka metrik, seharusnya diambil dari layanan analitik
        val pageViews = 12345
        val uniqueVisitors = 6789
        val bounceRate = 54.3f
        viewsText.text = getString(R.string.label_page_views) + ": $pageViews"
        visitorsText.text = getString(R.string.label_unique_visitors) + ": $uniqueVisitors"
        bounceText.text = getString(R.string.label_bounce_rate) + ": $bounceRate%"

        trendingList.layoutManager = LinearLayoutManager(this)

        Thread {
            val freq = AnalyticsService.fetchWordFrequency()
            val topics = freq.keys.take(10).toList()
            runOnUiThread {
                trendingList.adapter = TrendingTopicAdapter(topics)
                wordCloud.setWords(freq)
            }
        }.start()
    }
}
