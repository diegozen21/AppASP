package com.asp.loginhome.secciones.panel


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.asp.loginhome.R
import com.asp.loginhome.secciones.panel.documentos.Documentos
import com.asp.loginhome.secciones.panel.inventario.Inventario
import com.asp.loginhome.secciones.panel.mensajeria.Mensajeria
import com.asp.loginhome.secciones.panel.proyectos.Proyectos
import com.asp.loginhome.secciones.panel.tareas.Tareas
import com.asp.loginhome.secciones.panel.usuarios.Usuarios


class PanelFragment : Fragment(R.layout.fragment_panel) {

    private lateinit var sharedPreferences: SharedPreferences

    private val nombreLocal="nombre"
    private val apellidoPLocal="apellidoP"
    private val apellidoMLocal="apellidoM"
    private val rol="rol"

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_panel, container, false)

        sharedPreferences = requireContext().getSharedPreferences(PanelFragment.PREFS_FILE_NAME, Context.MODE_PRIVATE)


        val tvNombre = view.findViewById<TextView>(R.id.nombre_textview)

        val cardMensajeria = view.findViewById<CardView>(R.id.cardMensajeria)
        val cardProyectos = view.findViewById<CardView>(R.id.cardProyectos)
        val cardTareas = view.findViewById<CardView>(R.id.cardTareas)
        val cardDocumentos = view.findViewById<CardView>(R.id.cardDocumentos)
        val cardUsuarios = view.findViewById<CardView>(R.id.cardUsuarios)
        val cardInventario = view.findViewById<CardView>(R.id.cardInventario)

        val nombreAlmc = sharedPreferences.getString(nombreLocal, "")
        val apellidoPAlmc = sharedPreferences.getString(apellidoPLocal, "")
        val apellidoMAlmc = sharedPreferences.getString(apellidoMLocal, "")
        val rolAlmc = sharedPreferences.getString(rol, "")

        tvNombre.text = "$nombreAlmc $apellidoPAlmc $apellidoMAlmc"

        cardMensajeria.setOnClickListener {
            val intent = Intent(requireContext(), Mensajeria::class.java)
            startActivity(intent)
        }

        if (rolAlmc == "1" || rolAlmc == "2") {
            cardProyectos.visibility = View.VISIBLE
            cardProyectos.setOnClickListener {
                val intent = Intent(requireContext(), Proyectos::class.java)
                startActivity(intent)
            }
        }else{
            cardProyectos.visibility = View.GONE
        }

        if (rolAlmc == "1" || rolAlmc == "2") {
            cardTareas.visibility = View.VISIBLE
            cardTareas.setOnClickListener {
                val intent = Intent(requireContext(), Tareas::class.java)
                startActivity(intent)
            }
        }else{
            cardTareas.visibility = View.GONE
        }

        cardDocumentos.setOnClickListener {
            val intent = Intent(requireContext(), Documentos::class.java)
            startActivity(intent)
        }

        if (rolAlmc == "1" || rolAlmc == "2" || rolAlmc == "3" || rolAlmc == "5") {
            cardUsuarios.visibility=View.VISIBLE
            cardUsuarios.setOnClickListener {
                val intent = Intent(requireContext(), Usuarios::class.java)
                startActivity(intent)
            }
        }else{
            cardUsuarios.visibility=View.GONE
        }

        cardInventario.setOnClickListener{
            val intent = Intent(requireContext(), Inventario::class.java)
            startActivity(intent)
        }


        return view
    }

    companion object {
        private const val PREFS_FILE_NAME = "MyPrefs"
    }

}