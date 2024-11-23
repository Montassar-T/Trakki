package org.mdw32.trakki.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.mdw32.trakki.R

class CustomAdapter(
    context: Context,
    private val titles: List<String>,
    private val icons: List<Int>
) : ArrayAdapter<String>(context, 0, titles) {

    private var selectedPosition: Int = -1

    // Override getView() to handle the main list view (the visible dropdown item)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.dropdown_item, parent, false)
        val icon = view.findViewById<ImageView>(R.id.item_icon)
        val title = view.findViewById<TextView>(R.id.item_title)

        // Set the icon and title for the item
        icon.setImageResource(icons[position])
        title.text = titles[position]

        return view
    }

    // Override getDropDownView() to handle the dropdown items' appearance when expanded
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val title = view.findViewById<TextView>(R.id.item_title)
        val icon = view.findViewById<ImageView>(R.id.item_icon)

        // Highlight the selected item
        if (position == selectedPosition) {
            title.setTextColor(ContextCompat.getColor(context, R.color.green))
            icon.setColorFilter(ContextCompat.getColor(context, R.color.green))
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
        } else {
            title.setTextColor(ContextCompat.getColor(context, R.color.unselected))
            icon.setColorFilter(ContextCompat.getColor(context, R.color.unselected))
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.unselected))
        }

        return view
    }

    // Set the selected item position when it's selected
    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }
}
