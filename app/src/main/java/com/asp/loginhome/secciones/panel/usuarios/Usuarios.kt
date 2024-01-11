package com.asp.loginhome.secciones.panel.usuarios

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.widget.SearchView
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

import java.util.*

class Usuarios : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var usuariosAdapter: UsuariosAdapter
    private val listaUsuarios = mutableListOf<Usuario>()

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuarios)

        val btnCrearUsuario = findViewById<Button>(R.id.buttonCrearUsuario)

        btnCrearUsuario.setOnClickListener {
            val intent = Intent(this, CrearUsuario::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerViewUsuarios)
        recyclerView.layoutManager = LinearLayoutManager(this)
        usuariosAdapter = UsuariosAdapter(this, listaUsuarios)
        recyclerView.adapter = usuariosAdapter

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutUsuarios)
        swipeRefreshLayout.setOnRefreshListener {
            listaUsuarios.clear()
            obtenerUsuariosDesdeServidor()
            // Lógica para recargar los datos
        }

        obtenerUsuariosDesdeServidor()

        usuariosAdapter.setOnItemClickListener(object : UsuariosAdapter.OnItemClickListener {
            override fun onItemClick(usuario: Usuario) {
                val intent = Intent(this@Usuarios, JornadasUsuario::class.java)
                intent.putExtra("idUsuario", usuario.id)
                startActivity(intent)
            }
        })

        // Configurar el SearchView
        val searchView = findViewById<SearchView>(R.id.searchViewUsuarios)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                usuariosAdapter.filter(newText)
                return true
            }
        })
    }

    private fun obtenerUsuariosDesdeServidor() {
        val url = "${BaseApi.BaseURL}obtenerUsuarios.php"

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                procesarRespuestaJSON(response)
            },
            { error ->
                error.printStackTrace()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun procesarRespuestaJSON(response: JSONArray) {
        listaUsuarios.clear()
        for (i in 0 until response.length()) {
            val usuarioJSON: JSONObject = response.getJSONObject(i)
            val id = usuarioJSON.getString("id_Usuario")
            val nombre = usuarioJSON.getString("nombres")
            val apellido = usuarioJSON.getString("apellido_P")
            val usuario = Usuario(id, nombre, apellido)
            listaUsuarios.add(usuario)

            Log.d("Usuarios", "Usuario en posición $i - ID: $id, Nombre: $nombre, Apellido: $apellido")
        }

        // Notifica al adaptador después de cargar los usuarios
        usuariosAdapter.notifyDataSetChanged()
        usuariosAdapter.filter(null) // o usuariosAdapter.filter("")

        Log.d("Usuarios", "Total de usuarios: ${listaUsuarios.size}")
        // Detener la animación de actualización del SwipeRefreshLayout
        swipeRefreshLayout.isRefreshing = false
    }

    class UsuariosAdapter(
        private val context: Context,
        listaUsuarios: List<Usuario>
    ) : RecyclerView.Adapter<UsuariosAdapter.ViewHolder>() {

        private val listaUsuariosOriginal: List<Usuario>
        private val listaUsuariosFiltrada = mutableListOf<Usuario>()

        init {
            listaUsuariosOriginal = listaUsuarios
            listaUsuariosFiltrada.addAll(listaUsuariosOriginal)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val idTextView: TextView = itemView.findViewById(R.id.idUser)
            val nombreTextView: TextView = itemView.findViewById(R.id.nombreUser)
            val apellidoTextView: TextView = itemView.findViewById(R.id.apellidoUser)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val usuario = listaUsuariosFiltrada[position]
            holder.idTextView.text = usuario.id
            holder.nombreTextView.text = usuario.nombre
            holder.apellidoTextView.text = usuario.apellido

            holder.itemView.setOnClickListener {
                showPopupMenu(holder.itemView, usuario)
            }
        }
        private fun showPopupMenu(view: View, usuario: Usuario) {
            val popupMenu = PopupMenu(context, view)
            popupMenu.menuInflater.inflate(R.menu.usuario_options_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_ver_datos -> {
                        val intent = Intent(context, DatosUsuario::class.java)
                        intent.putExtra("idUsuario", usuario.id)
                        context.startActivity(intent)
                        true
                    }
                    R.id.menu_ver_jornada -> {
                        val intent = Intent(context, JornadasUsuario::class.java)
                        intent.putExtra("idUsuario", usuario.id)
                        context.startActivity(intent)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
        override fun getItemCount(): Int {
            return listaUsuariosFiltrada.size
        }

        private var onItemClickListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onItemClick(usuario: Usuario)
        }

        fun setOnItemClickListener(listener: OnItemClickListener) {
            onItemClickListener = listener
        }

        @SuppressLint("NotifyDataSetChanged")
        fun filter(query: String?) {
            listaUsuariosFiltrada.clear()
            if (!query.isNullOrBlank() && query.isNotEmpty()) {
                val lowerCaseQuery = query.lowercase()
                listaUsuariosOriginal.forEach {
                    if (it.nombre.lowercase().contains(lowerCaseQuery) || it.apellido.lowercase().contains(lowerCaseQuery)) {
                        listaUsuariosFiltrada.add(it)
                    }
                }
            } else {
                listaUsuariosFiltrada.addAll(listaUsuariosOriginal)
            }
            notifyDataSetChanged()
        }
    }

    data class Usuario(
        val id: String,
        val nombre: String,
        val apellido: String
    )

}
