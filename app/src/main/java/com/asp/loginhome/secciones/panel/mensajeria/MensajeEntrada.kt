package com.asp.loginhome.secciones.panel.mensajeria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.asp.loginhome.R

class MensajeEntrada : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensaje_entrada)

        // Obtener los datos del mensaje de la intent
        val idMensaje = intent.getStringExtra("id_Mensaje")
        val usuarioS = intent.getStringExtra("usuario_s")
        val asunto = intent.getStringExtra("asunto")
        val contenido = intent.getStringExtra("contenido")
        val fechaEnvio = intent.getStringExtra("fecha_Envio")
        val horaEnvio = intent.getStringExtra("hora_Envio")

        // Actualizar los TextViews u otros elementos de la interfaz de usuario con estos datos
        val textViewAsunto = findViewById<TextView>(R.id.textViewAsunto)
        val textViewRemitente = findViewById<TextView>(R.id.textViewRemitente)
        val textViewFecha = findViewById<TextView>(R.id.textViewFecha)
        val textViewHora = findViewById<TextView>(R.id.textViewHora)
        val textViewContenido = findViewById<TextView>(R.id.textViewContenido)

        textViewAsunto.text = asunto
        textViewRemitente.text = "Remitente: $usuarioS"
        textViewFecha.text = "Fecha: $fechaEnvio"
        textViewHora.text = "Hora: $horaEnvio"
        textViewContenido.text = contenido
    }
}
