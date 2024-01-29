package com.example.jokeapi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class JokeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joke_detail)
        displaySavedJokes()


        // Set up the back button click listener
        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            // Navigate back to the main activity
            finish()
        }
    }
    private fun displaySavedJokes() {
        val sharedPreferences = getSharedPreferences("SavedJokes", Context.MODE_PRIVATE)

        // Get the set of saved jokes
        val savedJokes = sharedPreferences.getStringSet("saved_jokes", HashSet()) as HashSet<String>

        // Create a StringBuilder to append the saved jokes
        val stringBuilder = StringBuilder()

        for (savedJoke in savedJokes) {
            stringBuilder.append(savedJoke).append("\n\n")
        }

        // Display the saved jokes in the TextView
        findViewById<TextView>(R.id.jokeDetailTextView).text = stringBuilder.toString()
    }
}