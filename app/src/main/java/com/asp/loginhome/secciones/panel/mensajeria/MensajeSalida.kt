package com.asp.loginhome.secciones.panel.mensajeria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.asp.loginhome.R

class MensajeSalida : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensaje_salida)

        // Obtener los datos del mensaje de la intent
        val idMensaje = intent.getStringExtra("id_Mensaje")
        val usuarioS = intent.getStringExtra("usuario_s")
        val asunto = intent.getStringExtra("asunto")
        val contenido = intent.getStringExtra("contenido")
        val fechaEnvio = intent.getStringExtra("fecha_Envio")
        val horaEnvio = intent.getStringExtra("hora_Envio")

        // Registrar los valores recibidos en el log
        Log.d("MensajeSalida", "idMensaje: $idMensaje")
        Log.d("MensajeSalida", "usuarioS: $usuarioS")
        Log.d("MensajeSalida", "asunto: $asunto")
        Log.d("MensajeSalida", "contenido: $contenido")
        Log.d("MensajeSalida", "fechaEnvio: $fechaEnvio")
        Log.d("MensajeSalida", "horaEnvio: $horaEnvio")

        // Actualizar los TextViews u otros elementos de la interfaz de usuario con estos datos
        val textViewAsunto = findViewById<TextView>(R.id.textViewAsunto)
        val textViewRemitente = findViewById<TextView>(R.id.textViewRemitente)
        val textViewFecha = findViewById<TextView>(R.id.textViewFecha)
        val textViewHora = findViewById<TextView>(R.id.textViewHora)
        val textViewContenido = findViewById<TextView>(R.id.textViewContenido)

        textViewAsunto.text = asunto
        textViewRemitente.text = "Destinatarios: $usuarioS"
        textViewFecha.text = "Fecha: $fechaEnvio"
        textViewHora.text = "Hora: $horaEnvio"
        textViewContenido.text = contenido
    }
}
