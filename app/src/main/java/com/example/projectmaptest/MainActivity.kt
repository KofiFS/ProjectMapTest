package com.example.projectmaptest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var placesClient: PlacesClient

    // Initialize database reference
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("locations")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the Places SDK
        Places.initialize(applicationContext, "AIzaSyDW7K6r6YZEV52mOta_7qFnB6kpr84nn0c")
        placesClient = Places.createClient(this)

        // Check if user is logged in
        if (isLoggedIn()) {
            // If logged in, show home fragment
            showHomeFragment()

        } else {
            // If not logged in, redirect to LoginActivity
            startActivity(Intent(this, LoginFirebase::class.java))
            finish()
        }
    }

    // Function to check if user is logged in
    private fun isLoggedIn(): Boolean {
        // Check if the current user is authenticated
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }

    // Function to show the home fragment
    private fun showHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()
    }
}
