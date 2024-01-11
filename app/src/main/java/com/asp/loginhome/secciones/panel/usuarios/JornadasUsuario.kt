package com.asp.loginhome.secciones.panel.usuarios

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.asp.loginhome.R
import com.asp.loginhome.recursos.BaseApi
import org.json.JSONArray
import org.json.JSONObject

class JornadasUsuario : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var jornadasAdapter: JornadasAdapter

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jornadas_usuario)


        recyclerView = findViewById(R.id.recyclerViewUsuarios)
        recyclerView.layoutManager = LinearLayoutManager(this)
        jornadasAdapter = JornadasAdapter(this, listaJornadas)
        recyclerView.adapter = jornadasAdapter

        val idUsuario = intent.getStringExtra("idUsuario")

        obtenerJornadasDesdeServidor(idUsuario.toString())

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutJornadasUsuario)
        swipeRefreshLayout.setOnRefreshListener {
            listaJornadas.clear()
            obtenerJornadasDesdeServidor(idUsuario.toString())
            // Lógica para recargar los datos
        }
    }

    class JornadasAdapter(
        private val context: Context, // Agrega el contexto como parámetro
        private val listaJornadas: List<Jornada>
    ) : RecyclerView.Adapter<JornadasAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val fechaTextView: TextView = itemView.findViewById(R.id.fechaJornada)
            val idJTextView: TextView = itemView.findViewById(R.id.idJornada)
            val horaInicioTextView: TextView = itemView.findViewById(R.id.horaInicioJ)
            val horaFinTextView: TextView = itemView.findViewById(R.id.horaFinJ)
            val ubiInicioTextView: TextView = itemView.findViewById(R.id.UbiIncioJ)
            val ubiFinTextView: TextView = itemView.findViewById(R.id.UbiFinJ)

            init {
                // Configura un OnClickListener para ubiInicioTextView
                ubiInicioTextView.setOnClickListener {
                    val ubicacionInicio = ubiInicioTextView.text.toString()
                    if (!ubicacionInicio.isNullOrEmpty()) {
                        abrirGoogleMapsConCoordenadas(ubicacionInicio)
                    }
                }

                // Configura un OnClickListener para ubiFinTextView
                ubiFinTextView.setOnClickListener {
                    val ubicacionFin = ubiFinTextView.text.toString()
                    if (!ubicacionFin.isNullOrEmpty()) {
                        abrirGoogleMapsConCoordenadas(ubicacionFin)
                    }
                }
            }
        }
        private fun abrirGoogleMapsConCoordenadas(coordenadas: String) {
            // Crea un Intent para abrir Google Maps con las coordenadas
            val gmmIntentUri = Uri.parse("https://maps.google.com/?q=$coordenadas&layer=c&cbll=$coordenadas")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

            // Especifica que deseas abrir Google Maps
            mapIntent.setPackage("com.google.android.apps.maps")

            // Verifica si hay una aplicación que pueda manejar el intent
            context.startActivity(mapIntent)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_jornada, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val usuario = listaJornadas[position]
            holder.fechaTextView.text = usuario.fecha
            holder.idJTextView.text = usuario.idJ
            holder.horaInicioTextView.text = usuario.horaInicio
            holder.horaFinTextView.text = usuario.horaFin

            // Verifica si las Horas son nulas o vacías antes de mostrarlas
            if (!usuario.horaInicio.isNullOrEmpty()) {
                holder.horaInicioTextView.text = usuario.horaInicio
                holder.horaInicioTextView.isEnabled = true
            } else {
                holder.horaInicioTextView.text = "No hay marcación"
                holder.horaInicioTextView.isEnabled = false
            }

            if (!usuario.horaFin.isNullOrEmpty()) {
                holder.horaFinTextView.text = usuario.horaFin
                holder.horaFinTextView.isEnabled = true
            } else {
                holder.horaFinTextView.text = "No hay marcación"
                holder.horaFinTextView.isEnabled = false
            }

            // Verifica si las ubicaciones son nulas o vacías antes de mostrarlas
            if (!usuario.ubiInicio.isNullOrEmpty()) {
                holder.ubiInicioTextView.text = usuario.ubiInicio
                holder.ubiInicioTextView.isEnabled = true
            } else {
                holder.ubiInicioTextView.text = "No hay marcación"
                holder.ubiInicioTextView.isEnabled = false
            }

            if (!usuario.ubiFin.isNullOrEmpty()) {
                holder.ubiFinTextView.text = usuario.ubiFin
                holder.ubiFinTextView.isEnabled = true
            } else {
                holder.ubiFinTextView.text = "No hay marcación"
                holder.ubiFinTextView.isEnabled = false
            }
        }

        override fun getItemCount(): Int {
            return listaJornadas.size
        }
    }

    data class Jornada(
        val fecha: String,
        val idJ: String,
        val horaInicio: String,
        val horaFin: String,
        val ubiInicio: String,
        val ubiFin: String
    )

    private val listaJornadas = mutableListOf<Jornada>()

    private fun obtenerJornadasDesdeServidor(idUsuario: String) {
        // Construir la URL con el parámetro idUsuario
        val url = "${BaseApi.BaseURL}obtenerJornadas.php?idUsuario=$idUsuario"

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                // Procesar la respuesta JSON para obtener datos de Jornadas
                procesarRespuestaJSON(response)
            },
            { error ->
                // Manejar errores de la solicitud Volley
                error.printStackTrace()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun procesarRespuestaJSON(response: JSONArray) {
        listaJornadas.clear()
        for (i in 0 until response.length()) {
            val usuarioJSON: JSONObject = response.getJSONObject(i)
            val fecha = usuarioJSON.getString("fecha_Jornada")
            val idJ = usuarioJSON.getString("id_Jornada")
            val horaInicio = usuarioJSON.getString("hora_Inicio")
            val horaFin = usuarioJSON.getString("hora_Fin")
            val ubiInicio = usuarioJSON.getString("ubicacion_Inicio")
            val ubiFin = usuarioJSON.getString("ubicacion_Fin")
            val jornada = Jornada(fecha, idJ, horaInicio, horaFin, ubiInicio, ubiFin)
            listaJornadas.add(jornada)

            Log.d("Jornadas", "Jornada en posición $i - ID: $idJ, Fecha: $fecha, Hora Inicio: $horaInicio y Ubicacion Inicio: $ubiInicio")

        }
        Log.d("Jornadas", "Total de jornadas: ${listaJornadas.size}")
        jornadasAdapter.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }


}