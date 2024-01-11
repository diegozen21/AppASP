package com.asp.loginhome.secciones.panel.usuarios

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.asp.loginhome.R
import com.asp.loginhome.recursos.BaseApi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CrearUsuario : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellidoPaterno: EditText
    private lateinit var editTextApellidoMaterno: EditText
    private lateinit var editTextIdUsuario: EditText
    private lateinit var editTextContrasena: EditText
    private lateinit var editTextEspecialidad: EditText
    private lateinit var editTextCargo: EditText
    private lateinit var editTextFechaNacimiento: TextView
    private lateinit var editTextCorreo: EditText
    private lateinit var radioGroupSexo: RadioGroup
    private lateinit var radioButtonMasculino: RadioButton
    private lateinit var radioButtonFemenino:RadioButton
    private lateinit var buttonCrearUsuario: Button
    private lateinit var spinnerRol: Spinner // Agregamos el Spinner
    private val cal = Calendar.getInstance()
    private val year = cal.get(Calendar.YEAR)
    private val month = cal.get(Calendar.MONTH)
    private val day = cal.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_usuario)

        // Inicializa las vistas
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextApellidoPaterno = findViewById(R.id.editTextApellidoPaterno)
        editTextApellidoMaterno = findViewById(R.id.editTextApellidoMaterno)
        editTextIdUsuario = findViewById(R.id.editTextIdUsuario)
        editTextContrasena = findViewById(R.id.editTextContrasena)
        editTextEspecialidad = findViewById(R.id.editTextEspecialidad)
        editTextCargo = findViewById(R.id.editTextCargo)
        editTextFechaNacimiento = findViewById(R.id.editTextFechaNacimiento)
        editTextCorreo = findViewById(R.id.editTextCorreoElectronico)
        radioGroupSexo = findViewById(R.id.radioGroupSexo)
        radioButtonMasculino = findViewById(R.id.radioButtonMasculino)
        radioButtonFemenino = findViewById(R.id.radioButtonFemenino)
        buttonCrearUsuario = findViewById(R.id.buttonCrearUsuario)
        spinnerRol = findViewById(R.id.spinnerRol) // Inicializamos el Spinner

        // Crear un InputFilter para permitir solo letras y espacios
        val lettersAndSpecialCharsFilter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isLetter(source[i]) &&
                    !Character.isWhitespace(source[i]) &&
                    source[i] != 'ñ' && source[i] != 'Ñ' && source[i] != 'á' && source[i] != 'é' &&
                    source[i] != 'í' && source[i] != 'ó' && source[i] != 'ú' && source[i] != 'Á' &&
                    source[i] != 'É' && source[i] != 'Í' && source[i] != 'Ó' && source[i] != 'Ú' &&
                    source[i] != '&' && source[i] != '(' && source[i] != ')' && source[i] != '-' &&
                    source[i] != '/' && source[i] != '[' && source[i] != ']' && source[i] != '{' &&
                    source[i] != '}' && source[i] != ';' && source[i] != ':' && source[i] != '.' &&
                    source[i] != ',' && source[i] != '<' && source[i] != '>' && source[i] != '"'
                ) {
                    return@InputFilter ""
                }
            }
            null // Aceptar el input
        }

// Aplicar el InputFilter a los campos de nombre, apellido paterno, apellido materno, especialidad y cargo
        editTextNombre.filters = arrayOf(lettersAndSpecialCharsFilter)
        editTextApellidoPaterno.filters = arrayOf(lettersAndSpecialCharsFilter)
        editTextApellidoMaterno.filters = arrayOf(lettersAndSpecialCharsFilter)
        editTextEspecialidad.filters = arrayOf(lettersAndSpecialCharsFilter)
        editTextCargo.filters = arrayOf(lettersAndSpecialCharsFilter)

        // Crear un InputFilter para idUsuario (letras y números, máximo 6 caracteres)
        val idUsuarioFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val input = source.subSequence(start, end).toString()
            if (input.matches("[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ]*".toRegex()) && dest.length + input.length - (dend - dstart) <= 6) {
                null // Aceptar el input
            } else {
                "" // Rechazar el input
            }
        }

// Aplicar el InputFilter al campo de idUsuario
        editTextIdUsuario.filters = arrayOf(idUsuarioFilter)

// Crear un InputFilter para la contraseña (máximo 8 caracteres)
        val passwordFilter = InputFilter.LengthFilter(8)

// Aplicar el InputFilter al campo de contraseña
        editTextContrasena.filters = arrayOf(passwordFilter)


        // Define las opciones para el Spinner (usuario y administrador)
        val opciones = arrayOf("Usuario", "Administrador")

        // Crea un ArrayAdapter para el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)

        // Configura el estilo del dropdown del Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asigna el ArrayAdapter al Spinner
        spinnerRol.adapter = adapter

        // Configura el listener de clic para el botón "Crear Usuario"
        buttonCrearUsuario.setOnClickListener {
            crearUsuario()
        }
        // Configura un listener para el Spinner para determinar el valor seleccionado
        spinnerRol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Obtiene el valor seleccionado
                val selectedItem = parent?.getItemAtPosition(position).toString()

                // Define una variable para el valor numérico
                if (selectedItem == "Usuario") 1 else 2

                // Puedes agregar código para hacer algo con el valor numerico si es necesario

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar el caso en el que no se seleccione nada
            }
        }

        editTextFechaNacimiento.setOnClickListener { showDatePickerDialog() }

    }

    private fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()

        // Calcula el año máximo permitido (2005, 18 años atrás desde el año actual)
        val maxYear = currentDate.get(Calendar.YEAR) - 18

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Crea un objeto Calendar con la fecha seleccionada
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                Log.d("Fecha Seleccionada: ", selectedDate.toString())
                Log.d("Fecha Actual: ", currentDate.toString())
                Log.d("Año Seleccionado: ", selectedYear.toString())
                Log.d("Año Maximo: ", maxYear.toString())

                // Compara con la fecha actual y el año máximo permitido
                if (selectedYear <= maxYear) {
                    // Si la fecha seleccionada es igual o posterior a la fecha actual y el año
                    // es más de 18 años atrás desde el año actual, la acepta
                    val formattedDate = formatDate(selectedDay, selectedMonth, selectedYear)
                    editTextFechaNacimiento.setText(formattedDate)
                } else {
                    // Si la fecha seleccionada es anterior a la fecha actual o el año no cumple
                    // con el requisito de edad, muestra un mensaje
                    mostrarMensaje("Selecciona una fecha válida (mayor de 18 años)")
                }
            },
            year, month, day
        )

        // Establece la fecha máxima permitida como el año máximo calculado
        datePickerDialog.datePicker.maxDate = currentDate.apply { set(Calendar.YEAR, maxYear) }.timeInMillis

        datePickerDialog.show()
    }

    private fun formatDate(day: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun crearUsuario() {
        // Obtiene los datos del formulario
        val nombre = editTextNombre.text.toString().trim()
        val apellidoPaterno = editTextApellidoPaterno.text.toString().trim()
        val apellidoMaterno = editTextApellidoMaterno.text.toString().trim()
        val idUsuario = editTextIdUsuario.text.toString().trim()
        val contrasena = editTextContrasena.text.toString().trim()
        val especialidad = editTextEspecialidad.text.toString().trim()
        val cargo = editTextCargo.text.toString().trim()
        val fechaNacimiento = editTextFechaNacimiento.text.toString().trim()
        val correo = editTextCorreo.text.toString().trim()
        val sexo = if (radioButtonMasculino.isChecked) "Masculino" else "Femenino"
        val rol = if (spinnerRol.selectedItem.toString() == "Usuario") 1 else 2

        // Valida que los campos no estén vacíos
        if (nombre.isEmpty() || apellidoPaterno.isEmpty() || idUsuario.isEmpty() ||
            contrasena.isEmpty() || especialidad.isEmpty() || cargo.isEmpty() ||
            fechaNacimiento.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Logs para rastrear datos antes de la solicitud
        Log.d("CrearUsuario", "Datos antes de la solicitud:")
        Log.d("CrearUsuario", "Nombre: $nombre")
        Log.d("CrearUsuario", "Apellido Paterno: $apellidoPaterno")
        Log.d("CrearUsuario", "Apellido Materno: $apellidoMaterno")
        Log.d("CrearUsuario", "ID de Usuario: $idUsuario")
        Log.d("CrearUsuario", "Contraseña: $contrasena")
        Log.d("CrearUsuario", "Especialidad: $especialidad")
        Log.d("CrearUsuario", "Cargo: $cargo")
        Log.d("CrearUsuario", "Fecha de Nacimiento: $fechaNacimiento")
        Log.d("CrearUsuario", "Correo: $correo")
        Log.d("CrearUsuario", "Sexo: $sexo")

        // URL de tu API para crear usuario
        val url = "${BaseApi.BaseURL}crearUsuario.php"

        // Parámetros a enviar en la solicitud POST
        val params = HashMap<String, String>()
        params["nombres"] = nombre
        params["apellido_paterno"] = apellidoPaterno
        params["apellido_materno"] = apellidoMaterno
        params["id_usuario"] = idUsuario
        params["contrasena"] = contrasena
        params["especialidad"] = especialidad
        params["cargo"] = cargo
        params["fecha_nacimiento"] = fechaNacimiento
        params["correo"] = correo
        params["sexo"] = sexo
        params["rol"] = rol.toString() // Agrega el parámetro "rol"

        // Configura la solicitud POST
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener<String> { response ->
                // Manejar la respuesta exitosa
                Log.d("CrearUsuario", "Respuesta exitosa: $response")
                Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                // Puedes agregar código para hacer otra acción después de crear el usuario
                val intent = Intent(applicationContext, Usuarios::class.java)
                startActivity(intent)
                // Cerrar la actividad de inicio de sesión
                finish()
            },
            Response.ErrorListener { error ->
                // Manejar errores de la solicitud
                Log.e("CrearUsuario", "Error en la solicitud: ${error.message}")
                Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        // Agrega la solicitud a la cola
        requestQueue.add(stringRequest)
    }
}

