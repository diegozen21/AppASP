package com.asp.loginhome.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.asp.loginhome.R
import com.asp.loginhome.recursos.BaseApi

class CambiarPassword : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_password)

        val editTextNewPassword = findViewById<EditText>(R.id.editTextNewPassword)
        val editTextConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)
        val buttonCambiarPassword = findViewById<Button>(R.id.buttonCambiarPassword)

        val email = intent.getStringExtra("email")

        buttonCambiarPassword.setOnClickListener {
            val newPassword = editTextNewPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword == confirmPassword) {
                // Las contraseñas coinciden, puedes proceder a enviar la nueva contraseña a la API
                if (email != null) {
                    // Envía la nueva contraseña y el correo a tu API (puedes implementar esto en tu API real)
                    sendNewPasswordToAPI(email, newPassword)
                }
            } else {
                showErrorDialog("Las contraseñas no coinciden. Inténtalo de nuevo.")
            }
        }
    }

    private fun sendNewPasswordToAPI(email: String, newPassword: String) {
        // Define la URL de tu API para cambiar la contraseña
        val apiUrl ="${BaseApi.BaseURL}cambiarContraseña.php?email=$email&newPassword=$newPassword"

        // Crea una solicitud GET utilizando Volley
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET, apiUrl,
            { response ->
                // Maneja la respuesta exitosa (puedes agregar lógica adicional si es necesario)
                // Suponemos que tu API devuelve una respuesta que indica si se cambió la contraseña correctamente
                if (response == "contrasena_cambiada_exitosamente") {
                    // Contraseña cambiada con éxito, puedes redirigir al usuario a la actividad de inicio de sesión.
                    Toast.makeText(this, "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, InicioSesion::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    // La contraseña no se cambió con éxito
                    showErrorDialog("Hubo un problema al cambiar la contraseña. Inténtalo de nuevo más tarde.")
                }
            },
            {
                // Maneja los errores de la solicitud a tu API
                showErrorDialog("Hubo un error al cambiar la contraseña. Inténtalo de nuevo más tarde.")
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
