package com.asp.loginhome.secciones.cuenta

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.asp.loginhome.R
import com.asp.loginhome.login.InicioSesion
import com.asp.loginhome.recursos.BaseApi
import com.asp.loginhome.secciones.cuenta.perfil.DatosPerfil
import com.asp.loginhome.secciones.cuenta.perfil.MisJornadas
import com.asp.loginhome.secciones.jornada.JornadaFragment
import com.asp.loginhome.secciones.panel.mensajeria.Mensajeria


class CuentaFragment : Fragment(R.layout.fragment_cuenta) {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var btnCerrarSesion: Button

    private lateinit var cardUsuario: CardView
    private lateinit var cardMisJornadas: CardView

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cuenta, container, false)

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val usuario = sharedPreferences.getString("idUsuario", "")

        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion)

        cardUsuario = view.findViewById(R.id.cardUsuario)
        cardMisJornadas = view.findViewById(R.id.cardMisJornadas)

        cardUsuario.setOnClickListener{
            val intent = Intent(requireContext(), DatosPerfil::class.java)
            startActivity(intent)
        }

        cardMisJornadas.setOnClickListener{
            val intent = Intent(requireContext(), MisJornadas::class.java)
            intent.putExtra("idUsuario", usuario)
            startActivity(intent)
        }

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        return view
    }

    private fun cerrarSesion() {
        val guardarCuenta = sharedPreferences.getString("guardarCuenta", "")
        val editor = sharedPreferences.edit()

        Log.d("guardarCuenta: ", guardarCuenta.toString())

        // Aquí puedes realizar las acciones necesarias para cerrar la sesión
        if (guardarCuenta == "true"){
            // Luego, redirige al usuario a la pantalla de inicio de sesión
            val intent = Intent(requireContext(), InicioSesion::class.java)
            startActivity(intent)
            requireActivity().finish() // Opcional: finaliza la actividad actual para evitar que el usuario pueda volver atrás
        }else{
            //limpiar los datos de usuario
            editor.clear().apply()
            // Luego, redirige al usuario a la pantalla de inicio de sesión
            val intent = Intent(requireContext(), InicioSesion::class.java)
            startActivity(intent)
            requireActivity().finish() // Opcional: finaliza la actividad actual para evitar que el usuario pueda volver atrás
        }

    }
}
