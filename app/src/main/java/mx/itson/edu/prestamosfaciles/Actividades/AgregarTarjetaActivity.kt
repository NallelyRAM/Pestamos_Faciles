package mx.itson.edu.prestamosfaciles.Actividades

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import mx.itson.edu.prestamosfaciles.Entidades.Tarjeta
import mx.itson.edu.prestamosfaciles.Entidades.User
import mx.itson.edu.prestamosfaciles.R


class AgregarTarjetaActivity : AppCompatActivity() {

    private val userRef = FirebaseFirestore.getInstance().collection("usuarios")

    // Seleccion spinners
    var emisorSeleccionado: String = ""
    var mesCaducidadSeleccionado: String = ""
    var anioCaducidadSeleccionado: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_tarjeta)
        val bundle = intent.extras

        var tarjeta: Tarjeta? = null

        try{
            tarjeta = intent.getSerializableExtra("tarjeta") as Tarjeta
        }catch(e: Exception){

        }

        llenadoSpinners(tarjeta)
        if(tarjeta != null){
            val et_numeroTarjeta: EditText = findViewById(R.id.et_numero_tarjeta)
            et_numeroTarjeta.isEnabled = false
            llenarCampos(tarjeta)
        }

        val btn_back: Button = findViewById(R.id.btn_back)
        val btn_aceptar: Button = findViewById(R.id.btn_aceptar)
        val et_numeroTarjeta: EditText = findViewById(R.id.et_numero_tarjeta)


        btn_aceptar.setOnClickListener { guardarTarjetaAUsuario(bundle) }

        btn_back.setOnClickListener { finish() }

        et_numeroTarjeta.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            private val space = " "
            private val maxDigits = 16

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // no se utiliza en este ejemplo
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // no se utiliza en este ejemplo
            }

            override fun afterTextChanged(s: Editable) {
                // Si la bandera de formateo está activada, no hagas nada
                if (isFormatting) {
                    return
                }

                // activa la bandera de formateo
                isFormatting = true

                // Elimina los espacios en blanco existentes
                val digits = s.toString().replace(space, "")

                // Comprueba que no se haya excedido el número máximo de dígitos
                if (digits.length > maxDigits) {
                    // Si se excedió, establece el texto anterior y el cursor en la última posición válida
                    et_numeroTarjeta.setText(s)
                    et_numeroTarjeta.setSelection(20)
                    return
                }

                // formatea el número con espacios cada 4 dígitos
                val formatted = StringBuilder()
                for (i in 0 until digits.length) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(space)
                    }
                    formatted.append(digits[i])
                }

                // Actualiza el texto del EditText
                et_numeroTarjeta.setText(formatted)
                // Establece el cursor al final del texto
                et_numeroTarjeta.setSelection(formatted.length)

                // desactiva la bandera de formateo
                isFormatting = false
            }
        })
    }


    private fun llenarCampos(tarjeta: Tarjeta){
        val et_numeroTarjeta: EditText = findViewById(R.id.et_numero_tarjeta)
        val et_nombrePropietario: EditText = findViewById(R.id.et_nombre_tarjeta)
        val et_apellidosPropietario: EditText = findViewById(R.id.et_apellidos_tarjeta)
        val et_codigoSeguridad: EditText = findViewById(R.id.et_codigo_seguridad)
        val et_codigoPostal: EditText = findViewById(R.id.et_codigo_postal)

        // Llenamos los campos edit text
        et_numeroTarjeta.setText(tarjeta.numTarjeta)
        et_nombrePropietario.setText(tarjeta.nombreTitular)
        et_apellidosPropietario.setText(tarjeta.apellidoTitular)
        et_codigoSeguridad.setText(tarjeta.CVV)
        et_codigoPostal.setText(tarjeta.codigoPostal)

        // Llenamos los campos Spinners

    }

    private fun guardarTarjetaAUsuario(bundle: Bundle?) {
        val et_numeroTarjeta: EditText = findViewById(R.id.et_numero_tarjeta)
        val et_nombrePropietario: EditText = findViewById(R.id.et_nombre_tarjeta)
        val et_apellidosPropietario: EditText = findViewById(R.id.et_apellidos_tarjeta)
        val et_codigoSeguridad: EditText = findViewById(R.id.et_codigo_seguridad)
        val et_codigoPostal: EditText = findViewById(R.id.et_codigo_postal)
        val numTarjeta = et_numeroTarjeta.text.toString()

        userRef.whereEqualTo("id", bundle?.getString("id"))
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val usuario = document.toObject(User::class.java)
                    val tarjetaExistente = usuario.tarjetas.find { it.numTarjeta == numTarjeta }

                    if (tarjetaExistente != null) { // Actualizar tarjeta existente

                        tarjetaExistente.mesCaducidad = mesCaducidadSeleccionado
                        tarjetaExistente.anioCaducidad = anioCaducidadSeleccionado
                        tarjetaExistente.codigoPostal = et_codigoPostal.text.toString()
                        tarjetaExistente.CVV = et_codigoSeguridad.text.toString()
                        tarjetaExistente.emisor = emisorSeleccionado
                        tarjetaExistente.nombreTitular = et_nombrePropietario.text.toString()
                        tarjetaExistente.apellidoTitular = et_apellidosPropietario.text.toString()

                        if (validaciones(tarjetaExistente)){
                            val usuarioRef = document.reference
                            usuarioRef.update(usuario.toMap())
                                .addOnSuccessListener {
                                    Log.d(TAG, "Se actualizó la tarjeta en el usuario correctamente.")
                                    Toast.makeText(this, "Se actualizó la tarjeta correctamente", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error al actualizar la tarjeta: $e")
                                }
                        }

                    } else { // Agregar nueva tarjeta
                        val tarjeta = Tarjeta()

                        tarjeta.numTarjeta = numTarjeta
                        tarjeta.mesCaducidad = mesCaducidadSeleccionado
                        tarjeta.anioCaducidad = anioCaducidadSeleccionado
                        tarjeta.nombreTitular = et_nombrePropietario.text.toString()
                        tarjeta.apellidoTitular = et_apellidosPropietario.text.toString()
                        tarjeta.codigoPostal = et_codigoPostal.text.toString()
                        tarjeta.CVV = et_codigoSeguridad.text.toString()
                        tarjeta.emisor = emisorSeleccionado

                        if (validaciones(tarjeta)){
                            usuario.addTarjeta(tarjeta)
                            val usuarioRef = document.reference
                            usuarioRef.update(usuario.toMap())
                                .addOnSuccessListener {
                                    Log.d(TAG, "Se ha agregado correctamente la tarjeta")
                                    Toast.makeText(this, "Se ha agregado correctamente la tarjeta", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error al guardar la tarjeta: $e")
                                }
                        }
                    }//agregar tarjeta
                }//for
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error al buscar el usuario: ", exception)
            }
    }

    private fun llenadoSpinners(tarjeta: Tarjeta?){
        // Llenamos el emisor de tarjetas
        val tipoTarjeta = arrayOf("Seleccione un emisor",
                                    "Visa",
                                    "Mastercard", "American Express",
                                    "Discover",
                                    "Dinner's Club")

        val adapterTarjeta = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipoTarjeta)
        adapterTarjeta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinnerTarjeta: Spinner = findViewById(R.id.sp_tipo_tarjeta)
        spinnerTarjeta.adapter = adapterTarjeta

        if(tarjeta != null){
            val position = adapterTarjeta.getPosition(tarjeta.emisor)
            spinnerTarjeta.setSelection(position)
        }

        spinnerTarjeta.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Emisor seleccionado
                emisorSeleccionado = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }

        // Llenamos los meses de caducidad
        val mesCaducidad = arrayOf("Mes",
                                    "01","02", "03","04","05","06","07","08","09","10","11","12")

        val adapterMesCaducidad = ArrayAdapter(this, android.R.layout.simple_spinner_item, mesCaducidad)
        adapterMesCaducidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinnerMesCaducidad: Spinner = findViewById(R.id.sp_mes_caducidad)
        spinnerMesCaducidad.adapter = adapterMesCaducidad

        if(tarjeta != null){
            val position = adapterMesCaducidad.getPosition(tarjeta.mesCaducidad)
            spinnerMesCaducidad.setSelection(position)
        }

        spinnerMesCaducidad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Mes seleccionado
                mesCaducidadSeleccionado = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }

        // Llenamos los años de caducidad
        val anioCaducidad = arrayOf("Año",
                                    "2023","2024", "2025","2026","2027","2028","2029","2030","2031","2032","2033","2034")

        val adapterAnioCaducidad = ArrayAdapter(this, android.R.layout.simple_spinner_item, anioCaducidad)
        adapterAnioCaducidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinnerAnioCaducidad: Spinner = findViewById(R.id.sp_año_caducidad)
        spinnerAnioCaducidad.adapter = adapterAnioCaducidad

        if(tarjeta != null){
            val position = adapterAnioCaducidad.getPosition(tarjeta.anioCaducidad)
            spinnerAnioCaducidad.setSelection(position)
        }

        spinnerAnioCaducidad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Año seleccionado
                anioCaducidadSeleccionado = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }
    }

    private fun validaciones(tarjeta: Tarjeta):Boolean{

        var numTarjeta = tarjeta.numTarjeta
        var et_nombrePropietario = tarjeta.nombreTitular
        var et_apellidosPropietario = tarjeta.apellidoTitular
        var et_codigoPostal = tarjeta.codigoPostal
        var et_codigoSeguridad = tarjeta.CVV

        if (emisorSeleccionado.equals("Seleccione un emisor")) {
            Toast.makeText(this, "Seleccione un emisor para continuar.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (numTarjeta.equals("") || numTarjeta.length < 19){
            Toast.makeText(this, "Debe agregar un número válido para continuar.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!validarNombre(et_nombrePropietario) || et_nombrePropietario.isEmpty() || et_nombrePropietario.length < 2){
            Toast.makeText(this, "Nombre/s incorrectos, vuelva a llenar los campos.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!validarNombre(et_apellidosPropietario) || et_apellidosPropietario.isEmpty() || et_apellidosPropietario.length < 2){
            Toast.makeText(this, "Apellido/s incorrectos, vuelva a llenar los campos.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (mesCaducidadSeleccionado == "Mes"){
            Toast.makeText(this, "Debe seleccionar un mes válido para continuar.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (anioCaducidadSeleccionado == "Año"){
            Toast.makeText(this, "Debe seleccionar un año válido para continuar.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (et_codigoSeguridad.isEmpty() || et_codigoSeguridad.length < 3){
            Toast.makeText(this, "Debe añadir un codigo de seguridad válido para continuar.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (et_codigoPostal.isEmpty() || et_codigoPostal.length < 5){
            Toast.makeText(this, "Debe añadir un codigo postal válido para continuar.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validarNombre(cadena: String):Boolean{
        val patron = Regex("[^A-Za-z\\s]")
        return !patron.containsMatchIn(cadena)
    }

}