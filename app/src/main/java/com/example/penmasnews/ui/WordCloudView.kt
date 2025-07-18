package com.example.penmasnews.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import kotlin.math.max

/**
 * Simple view that arranges words in a flexible layout with size
 * based on frequency, creating a basic word cloud effect.
 */
class WordCloudView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FlexboxLayout(context, attrs) {

    /**
     * Populate the view with words mapped to their occurrence counts.
     */
    fun setWords(freq: Map<String, Int>) {
        removeAllViews()
        if (freq.isEmpty()) return
        val maxCount = freq.values.maxOrNull() ?: 1
        for ((word, count) in freq) {
            val tv = TextView(context)
            val scale = 0.5f + count.toFloat() / max(1, maxCount)
            tv.text = word
            tv.textSize = 12f * scale
            val m = (4 * resources.displayMetrics.density).toInt()
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            lp.setMargins(m, m, m, m)
            addView(tv, lp)
        }
    }
}
