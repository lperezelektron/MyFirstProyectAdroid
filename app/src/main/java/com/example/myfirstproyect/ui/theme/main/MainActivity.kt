package com.example.myfirstproyect.ui.theme.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
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
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isGranted ->

        val message = when {
            isGranted -> "Permission Granted"
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> "Should show Rationales"
            else -> "Permission Denied"
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val moviesAdapter = MoviesAdapter(emptyList()) { navigateTo(it) }

        binding.recycler.adapter = moviesAdapter

        requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)

        lifecycleScope.launch {
            val apiKey = getString(R.string.api_key)
            val popularMovies = MovieDbClient.service.listPopularMovies(apiKey)
            moviesAdapter.movies = popularMovies.results
            moviesAdapter.notifyDataSetChanged()

        }
    }

    private fun navigateTo(movie: Movie) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie)
        startActivity(intent)
    }

}