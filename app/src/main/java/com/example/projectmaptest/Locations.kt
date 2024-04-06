package com.example.projectmaptest

// Define a data class Item to represent an item in the database
data class Locations(
    val id: String = "",             // Unique identifier for the item
    val savedLocationName: String = "",  // Title of the item
    val latitude: Number = 0.0,         // Latitude of the item
    val longitude: Number = 0.0,     // Longitude of the item
    val email: String = ""           // Email associated with the item
)