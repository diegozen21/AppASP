package com.asp.loginhome.inicio

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.asp.loginhome.R
import com.asp.loginhome.secciones.cuenta.CuentaFragment
import com.asp.loginhome.secciones.jornada.JornadaFragment
import com.asp.loginhome.secciones.panel.PanelFragment
import com.google.android.material.bottomnavigation.BottomNavigationView



@Suppress("DEPRECATION")
class PantallaPrincipal : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)


        val jornadaFragment = JornadaFragment()
        val cuentaFragment = CuentaFragment()
        val panelFragment = PanelFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.itemFichaje ->{
                    fragmentoActual(jornadaFragment)
                    true
                }
                R.id.itemInbox ->{
                    fragmentoActual(panelFragment)
                    true
                }
                R.id.itemCuenta ->{
                    fragmentoActual(cuentaFragment)
                    true
                }
                else -> false
            }

        }
    }

    private fun fragmentoActual (fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.contenedor, fragment)
            commit()
        }
    }

}