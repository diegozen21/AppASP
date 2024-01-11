package com.asp.loginhome.secciones.panel.proyectos

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.asp.loginhome.R
import com.asp.loginhome.recursos.BaseApi
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CrearProyecto : AppCompatActivity() {

    private lateinit var autoCompleteTextViewProyecto: AutoCompleteTextView
    private lateinit var multiAutoCompleteTextViewUsuarios: MultiAutoCompleteTextView
    private lateinit var editTextNombreTarea: EditText
    private lateinit var editTextDescripcionTarea: EditText
    private lateinit var editTextFechaEntrega: DatePicker
    private lateinit var editTextHoraEntrega: EditText
    private lateinit var buttonDesignar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_proyecto)

        // Inicializa las vistas
        autoCompleteTextViewProyecto = findViewById(R.id.multiAutoCompleteTextViewSupervisor)
        multiAutoCompleteTextViewUsuarios = findViewById(R.id.multiAutoCompleteTextViewUsuarios)
        editTextNombreTarea = findViewById(R.id.editTextNombreProyecto)
        editTextDescripcionTarea = findViewById(R.id.editTextDescripcion)
        editTextFechaEntrega = findViewById(R.id.datePickerFechaEntrega)
        buttonDesignar = findViewById(R.id.buttonGuardarProyecto)

        // Configura el separador para múltiples usuarios (en este caso, una coma)
        multiAutoCompleteTextViewUsuarios.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        // Carga la lista de proyectos al AutoCompleteTextView
        cargarListaProyectos()

        // Carga la lista de usuarios al MultiAutoCompleteTextView
        cargarListaUsuarios()

        // Configura el listener de clic para el botón "Designar"
        buttonDesignar.setOnClickListener {
            //designarTarea()
        }
    }

    private fun designarTarea() {
        // Obtiene los datos del formulario
        val proyecto = autoCompleteTextViewProyecto.text.toString().trim()
        val usuarios = multiAutoCompleteTextViewUsuarios.text.toString().trim()
        val nombreTarea = editTextNombreTarea.text.toString().trim()
        val descripcionTarea = editTextDescripcionTarea.text.toString().trim()
       // val fechaEntrega = editTextFechaEntrega.text.toString().trim()
        val horaEntrega = editTextHoraEntrega.text.toString().trim()
        val fechaCreacion = getCurrentDate()
        val horaCreacion = getCurrentTime()

        // Valida que los campos no estén vacíos
        if (usuarios.isEmpty() || nombreTarea.isEmpty() || descripcionTarea.isEmpty() || horaEntrega.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Divide la cadena de usuarios en un arreglo
        val usuariosArray = usuarios.split(",").map { it.trim() }

        // URL de tu API para designar tareas
        val url = "https://www.aspcontrol.com.pe/APP/designarTarea.php"

        // Parámetros a enviar en la solicitud POST
        val params = HashMap<String, String>()
        params["proyecto"] = proyecto
        params["usuarios"] = usuariosArray.joinToString(",") // Convierte a una cadena separada por comas
        params["nombreTarea"] = nombreTarea
        params["descripcionTarea"] = descripcionTarea
        params["fechaCreacion"] = fechaCreacion
        //params["fechaEntrega"] = fechaEntrega
        params["horaCreacion"] = horaCreacion
        params["horaEntrega"] = horaEntrega

        // Configura la solicitud POST
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener<String> { response ->
                // Manejar la respuesta exitosa
                Toast.makeText(this, "Tarea designada exitosamente", Toast.LENGTH_SHORT).show()
                // Puedes agregar código para hacer otra acción después de designar la tarea
            },
            Response.ErrorListener { error ->
                // Manejar errores de la solicitud
                Toast.makeText(this, "Error al designar la tarea", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        // Agrega la solicitud a la cola
        requestQueue.add(stringRequest)
    }

    private fun cargarListaProyectos() {
        // URL de tu API para obtener la lista de proyectos
        val urlProyectos = "${BaseApi.BaseURL}obtenerProyectos.php"

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlProyectos, null,
            { response ->
                // Procesa la respuesta JSON y obtén la lista de proyectos
                val listaProyectos = obtenerListaProyectos(response)
                Log.d("CargarListaProyectos", "Lista de proyectos: $listaProyectos")

                // Configura un adaptador para el AutoCompleteTextView
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listaProyectos)

                // Asocia el adaptador al AutoCompleteTextView de proyectos
                autoCompleteTextViewProyecto.setAdapter(adapter)

            },
            { error ->
                Log.e("CargarListaProyectos", "Error al cargar la lista de proyectos: ${error.message}")

                // Manejar errores de la solicitud
                Toast.makeText(this, "Error al cargar la lista de proyectos", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }
        )

        // Agrega la solicitud a la cola
        requestQueue.add(jsonArrayRequest)
    }

    private fun obtenerListaProyectos(response: JSONArray): ArrayList<Proyecto> {
        val listaProyectos = ArrayList<Proyecto>()
        for (i in 0 until response.length()) {
            try {
                val proyectoJSON = response.getJSONObject(i)
                val nombreProyecto = proyectoJSON.getString("nombreProyecto")
                val idProyecto = proyectoJSON.getString("idProyecto")

                listaProyectos.add(Proyecto(nombreProyecto, idProyecto))
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.e("ObtenerListaProyectos", "Error al procesar respuesta JSON: ${e.message}")

            }
        }
        Log.d("ObtenerListaProyectos", "Lista de proyectos obtenida: $listaProyectos")

        return listaProyectos
    }

    data class Proyecto(val nombre: String, val id: String) {
        override fun toString(): String {
            return "$id $nombre"
        }
    }


    private fun cargarListaUsuarios() {
        // URL de tu API para obtener la lista de usuarios
        val urlUsuarios = "${BaseApi.BaseURL}obtenerUsuarios.php"

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlUsuarios, null,
            { response ->
                // Procesa la respuesta JSON y obtén la lista de usuarios
                val listaUsuarios = obtenerListaUsuarios(response)
                Log.d("CargarListaUsuarios", "Lista de usuarios: $listaUsuarios")

                // Configura un adaptador para el MultiAutoCompleteTextView
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listaUsuarios)

                // Asocia el adaptador al MultiAutoCompleteTextView de usuarios
                multiAutoCompleteTextViewUsuarios.setAdapter(adapter)
            },
            { error ->
                Log.e("CargarListaUsuarios", "Error al cargar la lista de usuarios: ${error.message}")

                // Manejar errores de la solicitud
                Toast.makeText(this, "Error al cargar la lista de usuarios", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }
        )

        // Agrega la solicitud a la cola
        requestQueue.add(jsonArrayRequest)
    }

    private fun obtenerListaUsuarios(response: JSONArray): ArrayList<String> {
        val listaUsuarios = ArrayList<String>()
        for (i in 0 until response.length()) {
            try {
                val usuarioJSON = response.getJSONObject(i)
                val nombres = usuarioJSON.getString("nombres")
                val apellidoP = usuarioJSON.getString("apellido_P")
                val idUsuario = usuarioJSON.getString("id_Usuario")

                // Combina nombres y apellido_P en un solo nombre de usuario
                val nombreUsuario = "$nombres $apellidoP ( $idUsuario )"

                listaUsuarios.add(nombreUsuario)
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.e("ObtenerListaUsuarios", "Error al procesar respuesta JSON: ${e.message}")

            }
        }
        Log.d("ObtenerListaUsuarios", "Lista de usuarios obtenida: $listaUsuarios")

        return listaUsuarios
    }

    private fun getCurrentDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es"))
        return dateFormat.format(currentDate)
    }

    private fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(currentTime)
    }
}
