package com.example.myfirstproyect.ui.theme.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myfirstproyect.R
import com.example.myfirstproyect.databinding.ActivityMainBinding
import com.example.myfirstproyect.model.Movie
import com.example.myfirstproyect.model.MovieDbClient
import com.example.myfirstproyect.ui.theme.detail.DetailActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    companion object{
        private const val DEFAULT_REGION = "MX"
    }

    private val moviesAdapter = MoviesAdapter(emptyList()) { navigateTo(it) }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted ->
        requestPopularMovies(isGranted)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.recycler.adapter = moviesAdapter

        requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    private fun requestPopularMovies(isLocationGranted: Boolean) {
        if (isLocationGranted){
            fusedLocationClient.lastLocation.addOnCompleteListener{
                doRequestPopularMovies(getRegionFromLocation(it.result))
            }
        }else{
            doRequestPopularMovies(DEFAULT_REGION)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun doRequestPopularMovies(region: String) {
        lifecycleScope.launch {
            val apiKey = getString(R.string.api_key)
            val popularMovies = MovieDbClient.service.listPopularMovies(apiKey, region)
            moviesAdapter.movies = popularMovies.results
            moviesAdapter.notifyDataSetChanged()
        }
    }

    private fun getRegionFromLocation(location: Location?): String {
        if (location == null){
            return DEFAULT_REGION
        }
        val geocoder = Geocoder(this)
        val result = geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1
        )
        return result?.firstOrNull()?.countryCode ?: DEFAULT_REGION
    }

    private fun navigateTo(movie: Movie) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie)
        startActivity(intent)
    }

}