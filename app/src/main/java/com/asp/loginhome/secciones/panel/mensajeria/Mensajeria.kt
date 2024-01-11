package com.asp.loginhome.secciones.panel.mensajeria

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.asp.loginhome.R
import com.asp.loginhome.recursos.BaseApi
import com.asp.loginhome.secciones.panel.usuarios.Usuarios
import org.json.JSONArray
import org.json.JSONObject

class Mensajeria : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mensajesAdapter: MensajesAdapter
    private lateinit var searchView: SearchView

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensajeria)

        sharedPreferences = this.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)


        val btnNuevoMesaje = findViewById<Button>(R.id.buttonNuevoMensaje)

        btnNuevoMesaje.setOnClickListener{
            val intent = Intent(this, MensajeNuevo::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerViewMensajes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mensajesAdapter = MensajesAdapter(this, listaMensajes)
        recyclerView.adapter = mensajesAdapter



        mensajesAdapter.setOnItemClickListener(object : MensajesAdapter.OnItemClickListener {
            override fun onItemClick(mensaje: Mensaje) {
                // Agregar registros (logs) para verificar los valores enviados
                Log.d("MensajesAdapter", "id_Mensaje: ${mensaje.id_Mensaje}")
                Log.d("MensajesAdapter", "usuario_s: ${mensaje.usuario_s}")
                Log.d("MensajesAdapter", "asunto: ${mensaje.asunto}")
                Log.d("MensajesAdapter", "contenido: ${mensaje.contenido}")
                Log.d("MensajesAdapter", "fecha_Envio: ${mensaje.fecha_Envio}")
                Log.d("MensajesAdapter", "hora_Envio: ${mensaje.hora_Envio}")

                val radioButtonEntrada = findViewById<RadioButton>(R.id.radioButtonEntrada)

                // Verificar el estado del botón de radio
                if (radioButtonEntrada.isChecked) {
                    // El botón de radio "Entrada" está marcado, abrir VerMensajeEntrada
                    val intent = Intent(this@Mensajeria, MensajeEntrada::class.java)
                    // Pasar los datos del mensaje al intent
                    intent.putExtra("id_Mensaje", mensaje.id_Mensaje)
                    intent.putExtra("usuario_s", mensaje.usuario_s)
                    intent.putExtra("asunto", mensaje.asunto)
                    intent.putExtra("contenido", mensaje.contenido)
                    intent.putExtra("fecha_Envio", mensaje.fecha_Envio)
                    intent.putExtra("hora_Envio", mensaje.hora_Envio)
                    startActivity(intent)
                } else {
                    // El botón de radio "Salida" está marcado, abrir VerMensajeSalida
                    val intent = Intent(this@Mensajeria, MensajeSalida::class.java)
                    // Pasar los datos del mensaje al intent
                    intent.putExtra("id_Mensaje", mensaje.id_Mensaje)
                    intent.putExtra("usuario_s", mensaje.usuario_s)
                    intent.putExtra("asunto", mensaje.asunto)
                    intent.putExtra("contenido", mensaje.contenido)
                    intent.putExtra("fecha_Envio", mensaje.fecha_Envio)
                    intent.putExtra("hora_Envio", mensaje.hora_Envio)
                    startActivity(intent)
                }
            }
        })

        val searchViewMensajes = findViewById<SearchView>(R.id.searchViewMensajes)
        searchViewMensajes.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()){
                    mensajesAdapter.mostrarTodosLosMensajes()
                }else {
                    mensajesAdapter.filter(newText)
                }
                return true
            }
        })

        // Obtener las referencias a los botones de radio
        val radioButtonEntrada = findViewById<RadioButton>(R.id.radioButtonEntrada)
        val radioButtonSalida = findViewById<RadioButton>(R.id.radioButtonSalida)

        // Configurar listeners para los botones de radio
        radioButtonEntrada.setOnClickListener {
            listaMensajes.clear()
            obtenerMensajesEntradaDesdeServidor()
        }

        radioButtonSalida.setOnClickListener {
            listaMensajes.clear()
            obtenerMensajesSalidaDesdeServidor()
        }

        // Cargar los mensajes de entrada al inicio
        obtenerMensajesEntradaDesdeServidor()

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutMensajes)
        swipeRefreshLayout.setOnRefreshListener {
            if (radioButtonEntrada.isChecked) {
                listaMensajes.clear()
                obtenerMensajesEntradaDesdeServidor()
            } else {
                listaMensajes.clear()
                obtenerMensajesSalidaDesdeServidor()
            }
        }
    }


    class MensajesAdapter(
        private val context: AppCompatActivity,
        private val listaMensajes: List<Mensaje>
    ) : RecyclerView.Adapter<MensajesAdapter.ViewHolder>() {

        private val listaMensajesOriginal: List<Mensaje>
        private val listaMensajesFiltrada = mutableListOf<Mensaje>()

        init {
            listaMensajesOriginal = listaMensajes
            listaMensajesFiltrada.addAll(listaMensajesOriginal)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val idTextView: TextView = itemView.findViewById(R.id.idSender)
            val asuntoTextView: TextView = itemView.findViewById(R.id.asuntoMensaje)
            val fechaTextView: TextView = itemView.findViewById(R.id.fechaMensaje)
            val horaTextView: TextView = itemView.findViewById(R.id.horaMensaje)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_mensaje, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val mensaje = listaMensajesFiltrada[position]
            holder.idTextView.text = mensaje.usuario_s
            holder.asuntoTextView.text = mensaje.asunto
            holder.fechaTextView.text = mensaje.fecha_Envio
            holder.horaTextView.text = mensaje.hora_Envio

            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(mensaje)
            }
        }

        override fun getItemCount(): Int {
            return listaMensajesFiltrada.size
        }

        private var onItemClickListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onItemClick(mensaje: Mensaje)
        }

        fun setOnItemClickListener(listener: OnItemClickListener) {
            onItemClickListener = listener
        }

        fun filter(query: String?) {
            listaMensajesFiltrada.clear()
            if (!query.isNullOrBlank() && query.isNotEmpty()) {
                val lowerCaseQuery = query.lowercase()
                listaMensajesOriginal.forEach {
                    if (it.asunto.lowercase().contains(lowerCaseQuery) || it.usuario_s.lowercase().contains(lowerCaseQuery)) {
                        listaMensajesFiltrada.add(it)
                    }
                }
            } else {
                listaMensajesFiltrada.addAll(listaMensajesOriginal)
            }
            notifyDataSetChanged()
        }

        fun mostrarTodosLosMensajes() {
            listaMensajesFiltrada.clear()
            listaMensajesFiltrada.addAll(listaMensajesOriginal)
            notifyDataSetChanged()
        }

    }

    data class Mensaje(
        val id_Mensaje: String,
        val usuario_s: String,
        val asunto: String,
        val contenido: String,
        val hora_Envio: String,
        val fecha_Envio: String
    )


    private val listaMensajes = mutableListOf<Mensaje>()

    @SuppressLint("NotifyDataSetChanged")
    private fun obtenerMensajesEntradaDesdeServidor() {
        listaMensajes.clear()
        mensajesAdapter.notifyDataSetChanged()
        val idUsuario: String? = sharedPreferences.getString("idUsuario", null)
        val url = "${BaseApi.BaseURL}obtenerMensajesEntrada.php?idUsuario=$idUsuario"

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                try {
                    val mensajes = procesarRespuestaJSONentrada(response)
                    if (mensajes.isNotEmpty()) {
                        listaMensajes.clear()
                        listaMensajes.addAll(mensajes)
                        mensajesAdapter.mostrarTodosLosMensajes()
                        mensajesAdapter.notifyDataSetChanged()
                    } else {
                        listaMensajes.clear()

                        // Si no se obtienen mensajes, puedes mostrar un mensaje o realizar alguna otra acción.
                    }
                } catch (e: Exception) {
                    listaMensajes.clear()
                    // Manejar la excepción, por ejemplo, mostrando un mensaje de error
                    //e.printStackTrace()
                    Toast.makeText(this, "No se encontraron mensajes de entrada", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                // Manejar errores de red, por ejemplo, mostrando un mensaje de error
                Toast.makeText(this, "No se encontraron mensajes de entrada", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun obtenerMensajesSalidaDesdeServidor() {
        listaMensajes.clear()
        mensajesAdapter.notifyDataSetChanged()
        val idUsuario: String? = sharedPreferences.getString("idUsuario", null)
        val url = "${BaseApi.BaseURL}obtenerMensajesSalida.php?idUsuario=$idUsuario"

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                try {
                    val mensajes = procesarRespuestaJSONsalida(response)
                    if (mensajes.isNotEmpty()) {
                        listaMensajes.clear()
                        listaMensajes.addAll(mensajes)
                        mensajesAdapter.mostrarTodosLosMensajes()
                        mensajesAdapter.notifyDataSetChanged()
                    } else {
                        listaMensajes.clear()

                        // Si no se obtienen mensajes, puedes mostrar un mensaje o realizar alguna otra acción.
                    }
                } catch (e: Exception) {
                    listaMensajes.clear()

                    // Manejar la excepción, por ejemplo, mostrando un mensaje de error
                    e.printStackTrace()
                    Toast.makeText(this, "Error al obtener mensajes de salida", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                // Manejar errores de red, por ejemplo, mostrando un mensaje de error
                Toast.makeText(this, "No se encontraron mensajes de salida", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun procesarRespuestaJSONentrada(response: JSONArray): List<Mensaje> {
        listaMensajes.clear()
        val mensajes = mutableListOf<Mensaje>()
        for (i in 0 until response.length()) {
            val mensajeJSON: JSONObject = response.getJSONObject(i)
            val idMensaje = mensajeJSON.getString("id_Mensaje")
            val idRemitente = mensajeJSON.getString("id_Remitente")
            val asunto = mensajeJSON.getString("asunto")
            val contenido = mensajeJSON.getString("contenido")
            val horaEnvio = mensajeJSON.getString("hora_Envio")
            val fechaEnvio = mensajeJSON.getString("fecha_Envio")
            val mensaje = Mensaje(idMensaje, idRemitente, asunto, contenido, horaEnvio, fechaEnvio)
            mensajes.add(mensaje)
        }
        // Notifica al adaptador después de cargar los usuarios
        mensajesAdapter.mostrarTodosLosMensajes()

        mensajesAdapter.notifyDataSetChanged()
        // Detener la animación de actualización del SwipeRefreshLayout
        swipeRefreshLayout.isRefreshing = false
        Log.d("Usuarios", "Total de usuarios: ${listaMensajes.size}")
        return mensajes
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun procesarRespuestaJSONsalida(response: JSONArray): List<Mensaje> {
        listaMensajes.clear()

        val mensajes = mutableListOf<Mensaje>()
        for (i in 0 until response.length()) {
            val mensajeJSON: JSONObject = response.getJSONObject(i)
            val idMensaje = mensajeJSON.getString("id_Mensaje")
            val asunto = mensajeJSON.getString("asunto")
            val contenido = mensajeJSON.getString("contenido")
            val horaEnvio = mensajeJSON.getString("hora_Envio")
            val fechaEnvio = mensajeJSON.getString("fecha_Envio")

            // Obtener destinatarios (id_Usuario) del mensaje
            val destinatariosArray = mensajeJSON.getJSONArray("destinatarios")
            val destinatarios = mutableListOf<String>()
            for (j in 0 until destinatariosArray.length()) {
                val idDestinatario = destinatariosArray.getString(j)
                destinatarios.add(idDestinatario)
            }

            // Crear un mensaje con los destinatarios en formato de lista separados por comas
            val destinatariosComa = destinatarios.joinToString(", ")
            val mensaje = Mensaje(idMensaje, destinatariosComa, asunto, contenido, horaEnvio, fechaEnvio)
            mensajes.add(mensaje)
        }
        mensajesAdapter.mostrarTodosLosMensajes()
        // Notifica al adaptador después de cargar los usuarios
        mensajesAdapter.notifyDataSetChanged()

        // Detener la animación de actualización del SwipeRefreshLayout
        swipeRefreshLayout.isRefreshing = false

        Log.d("Usuarios", "Total de usuarios: ${listaMensajes.size}")
        return mensajes
    }



    companion object {
        private const val PREFS_FILE_NAME = "MyPrefs"
    }

}
