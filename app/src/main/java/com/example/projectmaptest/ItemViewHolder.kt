package com.example.projectmaptest

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference

// ViewHolder class for each item in the RecyclerView
class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // Function to bind item data to the ViewHolder
    fun bind(locations: Locations, databaseReference: DatabaseReference) {
        // Set the name of the item to the TextView
        itemView.findViewById<TextView>(R.id.itemName).text = locations.savedLocationName

        // Set OnClickListener to the remove button to remove the item
        itemView.findViewById<Button>(R.id.removeButton).setOnClickListener {
            removeItem(databaseReference, locations)
        }
    }
}

// Function to remove item from the database
private fun removeItem(databaseReference: DatabaseReference, locations: Locations) {
    // Get reference to the item in the database and remove it
    val itemRef = databaseReference.child(locations.id) // Assuming "title" is the unique identifier
    itemRef.removeValue()
}
