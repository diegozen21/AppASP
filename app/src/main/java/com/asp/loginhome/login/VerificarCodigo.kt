package com.asp.loginhome.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.asp.loginhome.R
import com.asp.loginhome.recursos.BaseApi

class VerificarCodigo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verificar_codigo)

        val editTextCodigo = findViewById<EditText>(R.id.editTextCodigo)
        val buttonVerificar = findViewById<Button>(R.id.buttonVerificar)

        val email = intent.getStringExtra("email")

        // Generar un código de verificación aleatorio de 6 caracteres (números y letras)
        val codigoGenerado = generateRandomCode()

        // Envía el código al correo electrónico proporcionado (puedes implementar esto en tu API real)
        if (email != null) {
            sendVerificationCodeToEmail(email, codigoGenerado)
        }

        buttonVerificar.setOnClickListener {
            val codigoIngresado = editTextCodigo.text.toString().trim()

            if (codigoIngresado.isNotEmpty() && codigoIngresado == codigoGenerado) {
                // El código ingresado coincide con el código generado, redirige al usuario a la actividad de cambio de contraseña.
                val intent = Intent(this, CambiarPassword::class.java)
                intent.putExtra("email", email)
                startActivity(intent)

                // Evita que el usuario regrese a esta actividad presionando el botón "Atrás"
                finish()
            } else {
                showErrorDialog("Código de verificación no válido.")
            }
        }
    }

    private fun generateRandomCode(): String {
        val characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val code = StringBuilder()
        val random = java.util.Random()

        for (i in 0 until 6) {
            val randomChar = characters[random.nextInt(characters.length)]
            code.append(randomChar)
        }

        return code.toString()
    }

    private fun sendVerificationCodeToEmail(email: String, code: String) {
        // Define la URL de tu API para enviar el código de verificación al correo
        val apiUrl = "${BaseApi.BaseURL}enviarCodigo.php?email=$email&code=$code"

        // Crea una solicitud GET utilizando Volley
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET, apiUrl,
            { response ->
                // Maneja la respuesta exitosa (puedes agregar lógica adicional si es necesario)
                // Suponemos que tu API devuelve una respuesta que indica si se envió correctamente
                if (response == "enviado_exitosamente") {
                    // La solicitud fue exitosa, el código se envió al correo
                } else {
                    // La solicitud fue exitosa, pero hubo un problema en el servidor
                    showErrorDialog("Hubo un problema al enviar el código de verificación. Inténtalo de nuevo más tarde.")
                }
            },
            {
                // Maneja los errores de la solicitud a tu API
                showErrorDialog("Hubo un error al enviar el código de verificación. Inténtalo de nuevo más tarde.")
            })

        // Agrega la solicitud a la cola
        requestQueue.add(stringRequest)
    }


    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
