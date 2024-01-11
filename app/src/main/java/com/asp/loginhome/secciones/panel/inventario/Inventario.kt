package com.asp.loginhome.secciones.panel.inventario

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.asp.loginhome.R
import com.asp.loginhome.login.InicioSesion
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Inventario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)

        val addMaterial = findViewById<FloatingActionButton>(R.id.fabAddMaterial)

        addMaterial.setOnClickListener{
            val intent = Intent(this, AgregarMaterial::class.java)
            startActivity(intent)
        }
    }
}