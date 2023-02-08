package com.example.tp3

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.internal.trimSubstring
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listestations: ArrayList<Station> = arrayListOf()

        val request = Request.Builder()
            .url(urlYelo)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val json = JSONObject(response.body!!.string())
                    val records = json.getJSONArray("records")

                    // On récupère les données via le Json pour en créer des objets Station, qu'on met dans la liste.
                    // Pour le nom, on enlève le premier élément en passant par un tableau (split / join) pour enlever le numéro au début
                    for (i in 0 until records.length()) {
                        val stationJson: JSONObject = records.getJSONObject(i)
                        val fields: JSONObject = stationJson.getJSONObject("fields")
                        val nom:String = fields.getString("station_nom")
                        val tabNoms = nom.split(" ")
                        val nomFinal = tabNoms.drop(1).joinToString(" ")
                        val dispo = fields.getInt("velos_disponibles")
                        val total = fields.getInt("nombre_emplacements")
                        val lon = fields.getDouble("station_longitude")
                        val lat = fields.getDouble("station_latitude")

                        val station: Station = Station(
                            nomFinal,
                            dispo,
                            total,
                            lon,
                            lat
                        )
                        listestations.add(station)
                    }
                }
                val bouton: Button = findViewById<Button>(R.id.bouton)
                val listView: ListView = findViewById<ListView>(R.id.listeitems)

                // On utilise ensuite cette liste dans un ListView pour afficher les données voulues
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    run() {
                        val listView: ListView = findViewById<ListView>(R.id.listeitems)
                        try {
                            val arrayAdapter = ArrayAdapter<Station>(
                                this@MainActivity,
                                android.R.layout.simple_list_item_1,
                                listestations
                            )
                            listView.adapter = arrayAdapter
                        } catch (e: IOException) {
                            e.printStackTrace();
                        }
                        // Listener de la liste pour pouvoir selectionner une station
                        listView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
                            val selectedItem = parent.getItemAtPosition(position) as Station

                            // On crée un intent de l'activity Maps qu'on lance, en passant en paramètre la station cliquée
                            val intent = Intent(this@MainActivity,MapsActivity::class.java).apply {
                                putExtra("station", selectedItem)
                            }
                            startActivity(intent)
                        })

                        // Listener sur le bouton, pour pouvoir afficher toutes les stations
                        bouton.setOnClickListener {
                            val intent = Intent(this@MainActivity,MapsActivity::class.java).apply {
                                putParcelableArrayListExtra("stations", listestations)
                            }
                            startActivity(intent)
                        }
                    }
                })
            }
        })
    }

    val client: OkHttpClient = OkHttpClient()
    val yelloKey: String = "" // API key Yello
    val urlYelo =  "https://api.agglo-larochelle.fr/production/opendata/api/records/1.0/search/dataset=yelo___disponibilite_des_velos_en_libre_service&facet=station_nom&api-key=$yelloKey"

}