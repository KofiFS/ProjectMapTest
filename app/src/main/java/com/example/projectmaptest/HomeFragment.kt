package com.example.projectmaptest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.location.Geocoder
import android.media.Image
import android.widget.Toast
import java.io.IOException
import androidx.core.view.GravityCompat
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class HomeFragment : Fragment(), OnMapReadyCallback {

    // Initialize variables
    private lateinit var mMap: GoogleMap
    private lateinit var mMapView: SupportMapFragment
    private val DEFAULT_LOCATION = LatLng(40.7128, -74.0060) // New York City coordinates
    private var currentDrawerTitle: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("locations")
    private lateinit var currentUserEmail: String
    private lateinit var textLongitudeValue: TextView
    private lateinit var textLatitudeValue: TextView
    private lateinit var imageviewSettings: ImageView


    // Google Places API client
    private lateinit var placesClient: PlacesClient

    private val PREFS_NAME = "UserLocationPrefs"
    private val PREF_LATITUDE = "Latitude"
    private val PREF_LONGITUDE = "Longitude"

    // Make sure to use the FloatingActionButton for all the FABs
    private lateinit var mAddFab: FloatingActionButton
    private lateinit var mSendCoordsFab: FloatingActionButton
    private lateinit var mASaveLocationFab: FloatingActionButton
    private lateinit var mOpenDrawerFab: FloatingActionButton

    // These are taken to make visible and invisible along with FABs
    private lateinit var SendCoordsText: TextView
    private lateinit var SaveLocationText: TextView
    private lateinit var OpenDrawerText: TextView

    // to check whether sub FAB buttons are visible or not.
    private var isAllFabsVisible: Boolean? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        textLatitudeValue = view.findViewById(R.id.textLatitudeValue) // Initialize textLatitudeValue
        textLongitudeValue = view.findViewById(R.id.textLongitudeValue) // Initialize textLongitudeValue
        imageviewSettings = view.findViewById(R.id.settingsImage)// Initialzie imageView
        // Initialize the map view
        mMapView = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mMapView.getMapAsync(this)

        // Initialize Google Places API client
        placesClient = Places.createClient(requireContext())


        // Register all the FABs with their IDs This FAB button is the Parent
        mAddFab = view.findViewById(R.id.add_fab)

        // FAB button
        mSendCoordsFab = view.findViewById(R.id.send_coords_fab)
        mASaveLocationFab = view.findViewById(R.id.save_location_fab)
        mOpenDrawerFab = view.findViewById(R.id.open_nav_fab)
        // Also register the action name text, of all the FABs.
        SendCoordsText = view.findViewById(R.id.send_coords_action_text)
        SaveLocationText = view.findViewById(R.id.save_location_action_text)
        OpenDrawerText = view.findViewById(R.id.open_nav_action_text)

        // Now set all the FABs and all the action name texts as GONE
        mSendCoordsFab.visibility = View.GONE
        mASaveLocationFab.visibility = View.GONE
        mOpenDrawerFab.visibility = View.GONE
        SendCoordsText.visibility = View.GONE
        SaveLocationText.visibility = View.GONE
        OpenDrawerText.visibility = View.GONE

        // make the boolean variable as false, as all the
        // action name texts and all the sub FABs are invisible
        isAllFabsVisible = false

        // We will make all the FABs and action name texts
        // visible only when Parent FAB button is clicked So
        // we have to handle the Parent FAB button first, by
        // using setOnClickListener you can see below
        mAddFab.setOnClickListener(View.OnClickListener {
            (if (!isAllFabsVisible!!) {
                // when isAllFabsVisible becomes true make all
                // the action name texts and FABs VISIBLE
                mSendCoordsFab.show()
                mASaveLocationFab.show()
                mOpenDrawerFab.show()
                SendCoordsText.visibility = View.VISIBLE
                SaveLocationText.visibility = View.VISIBLE
                OpenDrawerText.visibility = View.VISIBLE

                // make the boolean variable true as we
                // have set the sub FABs visibility to GONE
                true
            } else {
                // when isAllFabsVisible becomes true make
                // all the action name texts and FABs GONE.
                mSendCoordsFab.hide()
                mASaveLocationFab.hide()
                mOpenDrawerFab.hide()
                SendCoordsText.visibility = View.GONE
                SaveLocationText.visibility = View.GONE
                OpenDrawerText.visibility = View.GONE

                // make the boolean variable false as we
                // have set the sub FABs visibility to GONE
                false
            }).also { isAllFabsVisible = it }
        })
        // below is the sample action to handle add person FAB. Here it shows simple Toast msg.
        // The Toast will be shown only when they are visible and only when user clicks on them
        mASaveLocationFab.setOnClickListener {
            saveLocation()
            Toast.makeText(requireContext(), "User Location Saved", Toast.LENGTH_SHORT).show()
        }

        // below is the sample action to handle add alarm FAB. Here it shows simple Toast msg
        // The Toast will be shown only when they are visible and only when user clicks on them
        mSendCoordsFab.setOnClickListener {
            val latitude = loadLatitude()
            val longitude = loadLongitude()

            if (latitude != null && longitude != null) {
                // Assuming you want to send the location via an Intent
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Latitude: $latitude, Longitude: $longitude")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            } else {
                Toast.makeText(requireContext(), "Location data not available", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(requireContext(), "Coping Location", Toast.LENGTH_SHORT).show()
        }

        SaveLocationText.setOnClickListener {
            saveLocation()
            Toast.makeText(requireContext(), "User Location Saved", Toast.LENGTH_SHORT).show()
        }


        mOpenDrawerFab.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        // Set up AutoCompleteTextView
        //setupAutoCompleteTextView()

        // Set click listener for settings image
        imageviewSettings.setOnClickListener {
            openAppSettings()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navigationView = view.findViewById(R.id.nav_view)

        // Get the current user's email
        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

        // Set onClickListener for the logout button in the navigation drawer header
        navigationView.getHeaderView(0).findViewById<View>(R.id.logoutButton).setOnClickListener {
            logoutUser()
        }

    }
    private fun saveLocation() {
        // Save latitude and longitude in SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putFloat(PREF_LATITUDE, textLatitudeValue.text.toString().toFloat())
            putFloat(PREF_LONGITUDE, textLongitudeValue.text.toString().toFloat())
            apply()
        }
    }

    private fun loadLatitude(): Float? {
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getFloat(PREF_LATITUDE, 0f) // Default value is 0 if not found
    }

    private fun loadLongitude(): Float? {
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getFloat(PREF_LONGITUDE, 0f) // Default value is 0 if not found
    }


    private fun logoutUser() {
        // Log out the current user and redirect to LoginActivity
        FirebaseAuth.getInstance().signOut()
        activity?.let {
            it.startActivity(Intent(it, LoginFirebase::class.java))
            it.finish()
        }
    }
    private fun setupNavigationDrawer() {
        // Set up ActionBarDrawerToggle
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            activity as AppCompatActivity,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Set up navigation item selected listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Close the drawer when an item is clicked
            drawerLayout.closeDrawer(GravityCompat.START)

            // Handle navigation item clicks here
            val newTitle = menuItem.title.toString()
            if (newTitle != currentDrawerTitle) {
                currentDrawerTitle = newTitle
            }

            true
        }

        // Display user email in navigation drawer header
        val user = FirebaseAuth.getInstance().currentUser
        val userEmailTextView = navigationView.getHeaderView(0).findViewById<TextView>(R.id.userEmailTextView)
        userEmailTextView.text = user?.email ?: ""
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check if location permissions are granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
            return
        }

        // Load last saved location
        val lastLatitude = loadLatitude()
        val lastLongitude = loadLongitude()
        if (lastLatitude != null && lastLongitude != null) {
            val lastLatLng = LatLng(lastLatitude.toDouble(), lastLongitude.toDouble())
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 12f))
        } else {
            // If no last saved location, move camera to default location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 12f))
        }

        // Set up camera idle listener
        mMap.setOnCameraIdleListener {
            updateLocationInfo()
        }
    }
    private fun openAppSettings() {
        // Open app settings using intent
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }
    private fun updateLocationInfo() {
        val centerLatLng = mMap.cameraPosition.target
        val latitude = String.format(Locale.getDefault(), "%.6f", centerLatLng.latitude)
        val longitude = String.format(Locale.getDefault(), "%.6f", centerLatLng.longitude)

        textLatitudeValue.text = latitude
        textLongitudeValue.text = longitude
    }

}
