package com.example.projectmaptest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
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
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import androidx.appcompat.widget.SearchView

class HomeFragment : Fragment(), OnMapReadyCallback {

    // Initialize variables
    private lateinit var mMap: GoogleMap
    private lateinit var mMapView: SupportMapFragment
    private val DEFAULT_LOCATION = LatLng(40.7128, -74.0060) // New York City coordinates
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var currentUserEmail: String
    private lateinit var textLongitudeValue: TextView
    private lateinit var textLatitudeValue: TextView
    private lateinit var imageviewSettings: ImageView

    // Creating a variable for search view.
    private lateinit var searchView: SearchView

    // Google Places API client
    private lateinit var placesClient: PlacesClient

    // Sets the Preferfed Values
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

        // Initializing our search view.
        searchView = view.findViewById(R.id.idSearchView)

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
                // make the boolean variable true as we
                // have set the sub FABs visibility to GONE
                SendCoordsText.visibility = View.VISIBLE
                SaveLocationText.visibility = View.VISIBLE
                OpenDrawerText.visibility = View.VISIBLE
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

        // Adding on query listener for our search view.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Getting the location name from search view.
                val location = searchView.query.toString()

                // Creating a list of address where we will store the list of all addresses.
                var addressList: List<Address>? = null

                // Checking if the entered location is null or not.
                if (location != null || location != "") {
                    // Creating and initializing a geo coder.
                    val geocoder = Geocoder(requireContext())
                    try {
                        // Getting location from the location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    // Checking if address list is not null and not empty.
                    if (addressList != null && addressList.isNotEmpty()) {
                        // Getting the location from our list at the first position.
                        val address = addressList[0]

                        // Creating a variable for our location where we will add our locations latitude and longitude.
                        val latLng = LatLng(address.latitude, address.longitude)

                        // Adding marker to that position.
                        mMap.addMarker(MarkerOptions().position(latLng).title(location))

                        // Animating camera to that position.
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                    } else {
                        // Display a Toast message indicating that the location couldn't be found.
                        Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        // Calling our map fragment to update.
        mMapView.getMapAsync(this)

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
