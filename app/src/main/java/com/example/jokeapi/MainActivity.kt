    package com.example.jokeapi

    import android.content.Context
    import android.content.Intent
    import android.os.AsyncTask
    import android.os.Bundle
    import android.widget.Button
    import android.widget.CheckBox
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

            val anyCheckBox: CheckBox = findViewById(R.id.anycheckBox)
            val customCheckBox: CheckBox = findViewById(R.id.customcheckBox)
            val ProgrammingcheckBox: CheckBox = findViewById(R.id.ProgrammingcheckBox)
            val DarkcheckBox: CheckBox = findViewById(R.id.DarkcheckBox)

            var categorytext: String = ""
            // Set click listener for "Any" CheckBox
            anyCheckBox.setOnClickListener {
                if (anyCheckBox.isChecked) {
                    customCheckBox.isChecked = false
                    ProgrammingcheckBox.isEnabled = false
                    DarkcheckBox.isEnabled = false
                    ProgrammingcheckBox.isChecked = false
                    DarkcheckBox.isChecked = false
                    categorytext = "/Any"
                }
            }

            // Set click listener for "Custom" CheckBox
            customCheckBox.setOnClickListener {
                if (customCheckBox.isChecked) {
                    anyCheckBox.isChecked = false
                    ProgrammingcheckBox.isEnabled = true
                    DarkcheckBox.isEnabled = true
                }
            }
            ProgrammingcheckBox.setOnClickListener {
                if (ProgrammingcheckBox.isChecked) {
                    ProgrammingcheckBox.isChecked = true
                    if(DarkcheckBox.isChecked) {
                        categorytext = "/Programming,Dark"
                        Toast.makeText(this, categorytext, Toast.LENGTH_SHORT).show()

                    }else{
                        categorytext= "/Programming"
                        Toast.makeText(this, categorytext, Toast.LENGTH_SHORT).show()

                    }
                }else{
                    ProgrammingcheckBox.isChecked = false

                }
            }
            DarkcheckBox.setOnClickListener {
                if (DarkcheckBox.isChecked) {
                    DarkcheckBox.isChecked = true
                    if(ProgrammingcheckBox.isChecked) {
                        categorytext = "/Programming,Dark"
                        Toast.makeText(this, categorytext, Toast.LENGTH_SHORT).show()

                    }else{
                        categorytext= "/Dark"
                        Toast.makeText(this, categorytext, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    DarkcheckBox.isChecked = false

                }
            }
            val generateJokeButton: Button = findViewById(R.id.generateJokeButton)
            generateJokeButton.setOnClickListener {
                JokeAsyncTask(categorytext).execute()
            }

            val jokeDetailButton: Button = findViewById(R.id.jokedetailActivity)
            jokeDetailButton.setOnClickListener {
                val jokeDetailText = findViewById<TextView>(R.id.jokeTextView).text.toString()

                val intent = Intent(this, JokeDetailActivity::class.java)
                intent.putExtra("JOKE_DETAIL_TEXT", jokeDetailText)
                startActivity(intent)
            }
            val saveButton: Button = findViewById(R.id.saveButton)
            saveButton.setOnClickListener {
                val id = findViewById<TextView>(R.id.idTextView).text.toString()
                val category = findViewById<TextView>(R.id.categoryTextView).text.toString()
                val setup = findViewById<TextView>(R.id.setupTextView).text.toString()
                val joke = findViewById<TextView>(R.id.jokeTextView).text.toString()

                saveJoke(id, category, setup, joke)
            }
        }
        private fun saveJoke(id: String, category: String, setup: String, joke: String) {
            val sharedPreferences = getSharedPreferences("SavedJokes", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val savedJokes = sharedPreferences.getStringSet("saved_jokes", HashSet()) as HashSet<String>
            val currentJoke = "$id|$category|$setup|$joke"
            savedJokes.add(currentJoke)

            editor.putStringSet("saved_jokes", savedJokes)
            editor.apply()

            Toast.makeText(this, "Joke saved!", Toast.LENGTH_SHORT).show()
        }


        inner class JokeAsyncTask(private val categorytext: String) : AsyncTask<Void, Void, String>() {

            override fun doInBackground(vararg params: Void): String? {
                var result: String?
                var connection: HttpURLConnection? = null

                try {

                    var baseUrl = "https://v2.jokeapi.dev/joke"
                    var fullUrl = "$baseUrl$categorytext?idRange=1-100"

                    var url = URL(fullUrl)
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