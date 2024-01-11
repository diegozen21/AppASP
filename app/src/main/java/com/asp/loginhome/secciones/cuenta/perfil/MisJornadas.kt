package com.asp.loginhome.secciones.cuenta.perfil

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class MisJornadas : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var misJornadasAdapter: MisJornadasAdapter

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_jornadas)


        recyclerView = findViewById(R.id.recyclerViewMisJornadas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        misJornadasAdapter = MisJornadasAdapter(this, listaMisJornadas)
        recyclerView.adapter = misJornadasAdapter

        val idUsuario = intent.getStringExtra("idUsuario")

        obtenerMisJornadasDesdeServidor(idUsuario.toString())

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutMisJornadas)
        swipeRefreshLayout.setOnRefreshListener {
            listaMisJornadas.clear()
            obtenerMisJornadasDesdeServidor(idUsuario.toString())
            // Lógica para recargar los datos
        }
/*
        MisJornadasAdapter.setOnItemClickListener(object : MisJornadasAdapter.OnItemClickListener {
            override fun onItemClick(miJornada: MiJornada) {

            }
        })

 */
    }

    class MisJornadasAdapter(
        private val context: Context, // Agrega el contexto como parámetro
        private val listaMisJornadas: List<MiJornada>
    ) : RecyclerView.Adapter<MisJornadasAdapter.ViewHolder>() {

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
            val view = LayoutInflater.from(context).inflate(R.layout.item_mi_jornada, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val usuario = listaMisJornadas[position]
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

            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(usuario)
            }
        }

        override fun getItemCount(): Int {
            return listaMisJornadas.size
        }

        private var onItemClickListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onItemClick(mijornada: MiJornada)
        }

        fun setOnItemClickListener(listener: OnItemClickListener) {
            onItemClickListener = listener
        }

    }

    data class MiJornada(
        val fecha: String,
        val idJ: String,
        val reporteJ: String,
        val horaInicio: String,
        val horaFin: String,
        val ubiInicio: String,
        val ubiFin: String,
        val horaIniRef: String,
        val horaFinRef: String,
        val ubiIniRef: String,
        val ubiFinRef: String,
        val listaPausas: MutableList<MiPausa>
    )
    data class MiPausa(
        val idP: String,
        val reporteP: String,
        val horaPausa: String,
        val horaContinuar: String,
        val ubiPausa: String,
        val ubiContinuar: String
    )

    private val listaMisJornadas = mutableListOf<MiJornada>()

    private fun obtenerMisJornadasDesdeServidor(idUsuario: String) {
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
        listaMisJornadas.clear()

        for (i in 0 until response.length()) {
            val jornadaJSON: JSONObject = response.getJSONObject(i)

            // Obtener datos de la jornada
            val fecha = jornadaJSON.getString("fecha_Jornada")
            val idJ = jornadaJSON.getString("id_Jornada")
            val reporteJ = jornadaJSON.getString("reporte_Jornada")
            val horaInicio = jornadaJSON.getString("hora_Inicio")
            val horaFin = jornadaJSON.getString("hora_Fin")
            val ubiInicio = jornadaJSON.getString("ubicacion_Inicio")
            val ubiFin = jornadaJSON.getString("ubicacion_Fin")
            val horaIniRef = jornadaJSON.getString("hora_IniRefri")
            val horaFinRef = jornadaJSON.getString("hora_FinRefri")
            val ubiIniRef = jornadaJSON.getString("ubicacion_IniRefri")
            val ubiFinRef = jornadaJSON.getString("ubicacion_FinRefri")

            // Obtener el array de pausas de la jornada actual
            val pausasJSON = jornadaJSON.getJSONArray("pausas")
            val listaPausas = mutableListOf<MiPausa>()

            for (j in 0 until pausasJSON.length()) {
                val pausaJSON: JSONObject = pausasJSON.getJSONObject(j)

                // Obtener datos de la pausa
                val idP = pausaJSON.getString("id_Pausa")
                val reporteP = pausaJSON.getString("reporte_Pausa")
                val horaPausa = pausaJSON.getString("hora_Pausa")
                val horaContinuar = pausaJSON.getString("hora_Continuar")
                val ubiPausa = pausaJSON.getString("ubicacion_Pausa")
                val ubiContinuar = pausaJSON.getString("ubicacion_Continuar")

                // Crear objeto MiPausa y agregarlo a la lista de pausas
                val pausa = MiPausa(idP, reporteP, horaPausa, horaContinuar, ubiPausa, ubiContinuar)
                listaPausas.add(pausa)
            }

            // Crear objeto MiJornada y agregarlo a la lista de jornadas
            val jornada = MiJornada(
                fecha, idJ, reporteJ, horaInicio, horaFin, ubiInicio, ubiFin, horaIniRef, horaFinRef, ubiIniRef,ubiFinRef, listaPausas)
            listaMisJornadas.add(jornada)

            Log.d("Jornadas", "Jornada en posición $i - ID: $idJ, Fecha: $fecha, Hora Inicio: " +
                    "$horaInicio y Ubicacion Inicio: $ubiInicio")
        }

        Log.d("Jornadas", "Total de jornadas: ${listaMisJornadas.size}")
        misJornadasAdapter.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }


}