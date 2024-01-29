    package com.example.jokeapi

    import android.content.Context
    import android.content.Intent
    import android.os.AsyncTask
    import android.os.Bundle
    import android.widget.Button
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import org.json.JSONObject
    import java.io.BufferedReader
    import java.io.IOException
    import java.io.InputStream
    import java.io.InputStreamReader
    import java.net.HttpURLConnection
    import java.net.URL


    class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            // Set up the button click listener
            val generateJokeButton: Button = findViewById(R.id.generateJokeButton)
            generateJokeButton.setOnClickListener {
                // Execute the AsyncTask to make the API call
                JokeAsyncTask().execute()
            }

            val jokeDetailButton: Button = findViewById(R.id.jokedetailActivity)
            jokeDetailButton.setOnClickListener {
                // Retrieve the joke detail text from the TextView
                val jokeDetailText = findViewById<TextView>(R.id.jokeTextView).text.toString()

                // Start the JokeDetailActivity and pass the joke detail text
                val intent = Intent(this, JokeDetailActivity::class.java)
                intent.putExtra("JOKE_DETAIL_TEXT", jokeDetailText)
                startActivity(intent)
            }
            val saveButton: Button = findViewById(R.id.saveButton)
            saveButton.setOnClickListener {
                // Retrieve the joke information
                val id = findViewById<TextView>(R.id.idTextView).text.toString()
                val category = findViewById<TextView>(R.id.categoryTextView).text.toString()
                val setup = findViewById<TextView>(R.id.setupTextView).text.toString()
                val joke = findViewById<TextView>(R.id.jokeTextView).text.toString()

                // Save the joke information using SharedPreferences
                saveJoke(id, category, setup, joke)
            }
        }
        private fun saveJoke(id: String, category: String, setup: String, joke: String) {
            val sharedPreferences = getSharedPreferences("SavedJokes", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            // Get the existing saved jokes
            val savedJokes = sharedPreferences.getStringSet("saved_jokes", HashSet()) as HashSet<String>

            // Create a new string representing the current joke
            val currentJoke = "$id|$category|$setup|$joke"

            // Add the current joke to the set of saved jokes
            savedJokes.add(currentJoke)

            // Save the updated set of jokes
            editor.putStringSet("saved_jokes", savedJokes)
            editor.apply()

            Toast.makeText(this, "Joke saved!", Toast.LENGTH_SHORT).show()
        }


        inner class JokeAsyncTask : AsyncTask<Void, Void, String>() {

            override fun doInBackground(vararg params: Void): String? {
                var result: String?
                var connection: HttpURLConnection? = null

                try {
                    val url = URL("https://v2.jokeapi.dev/joke/Any?idRange=1-100")
                    connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"

                    val inputStream: InputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    result = stringBuilder.toString()
                } catch (e: IOException) {
                    result = null
                } finally {
                    connection?.disconnect()
                }
                return result
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                if (result != null) {
                    // Parse the JSON response

                    val jokeObject = JSONObject(result)
                    val type = jokeObject.getString("type")

                    if(type == "twopart")
                    {
                        val setup = jokeObject.getString("setup")
                        val delivery = jokeObject.getString("delivery")
                        findViewById<TextView>(R.id.jokeTextView).text = "Joke:  $delivery"
                        findViewById<TextView>(R.id.setupTextView).text = "Setup: $setup"

                    }
                    else
                    {
                        val joke = jokeObject.getString("joke")
                        findViewById<TextView>(R.id.jokeTextView).text = "Joke: $joke"

                    }
                    val id = jokeObject.getString("id")
                    val category = jokeObject.getString("category")
                    // Update UI with the retrieved data
                    findViewById<TextView>(R.id.categoryTextView).text = "Category: $category"
                    findViewById<TextView>(R.id.idTextView).text = "id: $id"


                }
            }
        }
    }