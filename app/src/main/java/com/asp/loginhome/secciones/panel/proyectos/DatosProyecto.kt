package com.asp.loginhome.secciones.panel.proyectos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.asp.loginhome.R

class DatosProyecto : AppCompatActivity() {

    private lateinit var txtNombreProyecto: TextView
    private lateinit var txtEtapaProyecto: TextView
    private lateinit var txtCodigoProyecto: TextView
    private lateinit var txtDescripcionProyecto: TextView
    private lateinit var txtSupervisorProyecto: TextView
    private lateinit var txtTiempoDesarrollo: TextView
    private lateinit var txtFechaEntrega: TextView
    private lateinit var txtFechaCreacion: TextView
    private lateinit var txtListaMateriales: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_proyecto)
    }
}