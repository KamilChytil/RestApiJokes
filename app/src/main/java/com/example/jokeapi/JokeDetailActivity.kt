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


        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }
    private fun displaySavedJokes() {
        val sharedPreferences = getSharedPreferences("SavedJokes", Context.MODE_PRIVATE)

        val savedJokes = sharedPreferences.getStringSet("saved_jokes", HashSet()) as HashSet<String>

        val stringBuilder = StringBuilder()

        for (savedJoke in savedJokes) {
            stringBuilder.append(savedJoke).append("\n\n")
        }

        findViewById<TextView>(R.id.jokeDetailTextView).text = stringBuilder.toString()
    }
};