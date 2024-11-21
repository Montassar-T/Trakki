package org.mdw32.trakki.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import org.mdw32.trakki.R

class DotIndicatorView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var currentPosition = 0

    init {
        orientation = HORIZONTAL
    }

    // Setup the dot indicators (inactive initially)
    fun setupIndicator(dotCount: Int) {
        removeAllViews() // Clear any existing dots

        for (i in 0 until dotCount) {
            val dot = createDotView(isActive = i == 0) // Mark the first dot as active
            addView(dot)
        }
    }

    // Create a dot view with custom size and color depending on whether it's active or not
    private fun createDotView(isActive: Boolean): ImageView {
        val dot = ImageView(context)
        val params = if (isActive) {
            LayoutParams(
                resources.getDimensionPixelSize(R.dimen.dot_size_active_width),
                resources.getDimensionPixelSize(R.dimen.dot_size_active_height)
            )
        } else {
            LayoutParams(
                resources.getDimensionPixelSize(R.dimen.dot_size_inactive_width),
                resources.getDimensionPixelSize(R.dimen.dot_size_inactive_width)
            )
        }

        dot.layoutParams = params
        val drawable = if (isActive) {
            ContextCompat.getDrawable(context, R.drawable.active_dot) // Active state
        } else {
            ContextCompat.getDrawable(context, R.drawable.inactive_dot) // Inactive state
        }
        dot.setImageDrawable(drawable)
        return dot
    }

    // Update the dot indicator to show the active state and update sizes
    fun updateDotIndicator(position: Int) {
        for (i in 0 until childCount) {
            val dot = getChildAt(i) as ImageView
            val isActive = i == position
            val drawable = if (isActive) {
                ContextCompat.getDrawable(context, R.drawable.active_dot)
            } else {
                ContextCompat.getDrawable(context, R.drawable.inactive_dot)
            }

            // Directly set the drawable for the dot
            dot.setImageDrawable(drawable)

            // Update the layout parameters to adjust the size based on whether the dot is active or not
            val params = if (isActive) {
                LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.dot_size_active_width),
                    resources.getDimensionPixelSize(R.dimen.dot_size_active_height)
                )
            } else {
                LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.dot_size_inactive_width),
                    resources.getDimensionPixelSize(R.dimen.dot_size_inactive_width)
                )
            }
            dot.layoutParams = params
        }
    }
}
