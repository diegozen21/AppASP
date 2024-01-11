package com.asp.loginhome.secciones.cuenta.perfil

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.asp.loginhome.R
import com.asp.loginhome.secciones.cuenta.CuentaFragment

class DatosPerfil : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private val nombreLocal="nombre"
    private val apellidoPLocal="apellidoP"
    private val apellidoMLocal="apellidoM"
    private val areaLocal="area"
    private val especialidadLocal="especialidad"

    private lateinit var tvIdUsuario: TextView
    private lateinit var tvnombre: TextView
    private lateinit var tvapellidos: TextView
    private lateinit var tvespecialidad: TextView
    private lateinit var tvcargo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_perfil)

        sharedPreferences = this.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

        tvIdUsuario = findViewById(R.id.idUsuario)
        tvnombre = findViewById(R.id.nombreUsuario)
        tvapellidos = findViewById(R.id.apellidoUsuario)
        tvespecialidad = findViewById(R.id.especialidadUsuario)
        tvcargo = findViewById(R.id.cargoUsuario)

        val nombreAlmc = sharedPreferences.getString(nombreLocal, "")
        val apellidoPAlmc = sharedPreferences.getString(apellidoPLocal, "")
        val apellidoMAlmc = sharedPreferences.getString(apellidoMLocal, "")
        val areaAlmc = sharedPreferences.getString(areaLocal, "")
        val especialidadAlmc = sharedPreferences.getString(especialidadLocal, "")
        val idUsuario = sharedPreferences.getString("idUsuario", "")

        if (idUsuario != "null") {
            tvIdUsuario.text = idUsuario
        }

        if (nombreAlmc != "null") {
            tvnombre.text = nombreAlmc
        }

        if (apellidoPAlmc != "null" && apellidoMAlmc != "null") {
            tvapellidos.text = "$apellidoPAlmc $apellidoMAlmc"
        }

        if (especialidadAlmc != "null") {
            tvespecialidad.text = especialidadAlmc
        }

        if (areaAlmc != "null") {
            tvcargo.text = areaAlmc
        }

    }
    companion object {
        private const val PREFS_FILE_NAME = "MyPrefs"
    }
}