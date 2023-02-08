package com.example.tp3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.tp3.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        // CAS 1 : une seule station >

        // On récupère l'objet Station, récupéré de l'activité Main via le Parcelable
        val station: Station? = intent.getParcelableExtra<Station>("station")
        if(station != null) {
            // On pose le marqueur et on zoom et recadre dessus
            val point = LatLng(station!!.lat, station.lon!!)
            mMap.addMarker(
                MarkerOptions().position(point).title(station.nom)
                    .snippet("${station.dispo}/${station.total} place(s)")
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 13f))

            // On affiche le nom de la station également au dessus de la carte
            val infos: TextView = findViewById<TextView>(R.id.infosmap)
            infos.setText(station.nom)
        }

        // CAS 2 : toutes les stations >

        // On récupère la liste de stations qu'on affiche une à une
        val stations: ArrayList<Station>? = intent.getParcelableArrayListExtra<Station>("stations")
        if(stations != null) {
            var totalLat: Double = 0.0
            var totalLong: Double = 0.0

            for(i in 0 until stations.size) {
                val station: Station = stations.get(i)
                totalLat += station.lat
                totalLong += station.lon!!
                val point = LatLng(station!!.lat, station.lon!!)
                mMap.addMarker(
                    MarkerOptions().position(point).title(station.nom)
                        .snippet("${station.dispo}/${station.total} place(s)")
                )
            }
            // On calcule la moyenne des latitudes et longitudes pour que la carte soit bien centrée (et zoomée)
            val avgLat:Double = totalLat / stations.size
            val avgLong:Double = totalLong / stations.size

            val pointAvg = LatLng(avgLat, avgLong)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointAvg, 13f))
        }
    }
}