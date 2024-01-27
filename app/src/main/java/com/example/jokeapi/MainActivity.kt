    package com.example.jokeapi

    import android.os.AsyncTask
    import android.os.Bundle
    import android.widget.TextView
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

            // Execute the AsyncTask to make the API call
            JokeAsyncTask().execute()
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