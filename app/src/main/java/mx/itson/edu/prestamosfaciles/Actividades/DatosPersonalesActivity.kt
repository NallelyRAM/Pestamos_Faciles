package mx.itson.edu.prestamosfaciles.Actividades

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import mx.itson.edu.prestamosfaciles.Entidades.User
import mx.itson.edu.prestamosfaciles.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DatosPersonalesActivity : AppCompatActivity() {

    private val userRef = FirebaseFirestore.getInstance().collection("usuarios")

    var nombre: String? = ""
    var apellidos: String? = ""
    var fechaNacimiento: Timestamp? = Timestamp(Date())
    var telefono: String? = ""
    var correo: String? = ""
    var ubicacion: String? = ""
    var id = ""
    var photoURI: Uri? = null

    @SuppressLint("WrongViewCast", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datos)

        val bundle = intent.extras

        val tv_nombre: TextView = findViewById(R.id.tv_misDatosNombre)
        val tv_apellidos: TextView = findViewById(R.id.tv_misDatosApellidos)
        val tv_fechaNacimiento: TextView = findViewById(R.id.tv_misDatosFechaNacimiento)
        val tv_telefono: TextView = findViewById(R.id.tv_telefono)
        val tv_correo: TextView = findViewById(R.id.tv_Correo)
        val sp_ubicacion: Spinner = findViewById(R.id.sp_categoria_colonia)

        val btnBack: Button = findViewById(R.id.btn_back)

        tv_fechaNacimiento.setOnClickListener { mostrarDatePicker() }

        if(bundle != null) {
            photoURI = bundle?.getParcelable("photo")
            id = bundle.getString("id").toString()
            val query = userRef.whereEqualTo("id", id) // construye una consulta para buscar el usuario con el nombre dado como parámetro

            query.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.id // obtiene el ID del documento
                    val data = document.data // obtiene los datos del documento

                    val user = User(
                        id = id,
                        nombre = data["nombre"] as? String,
                        apellido = data["apellido"] as? String,
                        correo = data["correo"] as? String,
                        telefono = data["telefono"] as? String,
                        fechaNacimiento = data["fechaNacimiento"] as? Timestamp,
                        ubicacion = data["ubicacion"] as? String
                    )

                    nombre = user.nombre
                    apellidos = user.apellido
                    fechaNacimiento = user.fechaNacimiento
                    telefono = user.telefono
                    correo = user.correo
                    ubicacion = user.ubicacion

                    tv_nombre.text = nombre
                    tv_apellidos.text = apellidos
                    tv_fechaNacimiento.text = fechaNacimiento?.let { secondsToDate(fechaNacimiento!!.seconds) } ?: ""
                    tv_telefono.text = telefono
                    tv_correo.text = correo

                    val dataSpinner: Array<String> = llenarSpinner()
                    val adapterUbicacion = ArrayAdapter(this, android.R.layout.simple_spinner_item, dataSpinner)
                    val editTextSearch: EditText = findViewById(R.id.editTextSearch)
                    adapterUbicacion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    sp_ubicacion.adapter = adapterUbicacion

                    val position = adapterUbicacion.getPosition(ubicacion)
                    sp_ubicacion.setSelection(position)

                    // Configurar el TextWatcher para realizar la búsqueda en el Spinner
                    editTextSearch.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(editable: Editable?) {
                            val searchText = editable.toString().toLowerCase()

                            // Filtrar los datos del Spinner en base al texto de búsqueda
                            val filteredData = dataSpinner.filter { item ->
                                item.lowercase().contains(searchText)
                            }

                            // Actualizar el adaptador del Spinner con los datos filtrados
                            val filteredAdapter = ArrayAdapter<String>(this@DatosPersonalesActivity, android.R.layout.simple_spinner_item, filteredData)
                            filteredAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            sp_ubicacion.adapter = filteredAdapter
                        }
                    })

                    sp_ubicacion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            ubicacion = parent.getItemAtPosition(position) as String
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // No hacer nada
                        }
                    }

                }
            }.addOnFailureListener { exception ->
                // maneja la excepción si no se puede completar la consulta
            }
        }
        val btnFinalizar = findViewById<TextView>(R.id.btn_inicioFinalizar)
        btnFinalizar.setOnClickListener{

            //AQUI
            if(validaciones()) {

                val usuarioActualizado = hashMapOf(
                    "nombre" to tv_nombre.text.toString(),
                    "apellido" to tv_apellidos.text.toString(),
                    "telefono" to tv_telefono.text.toString(),
                    "fechaNacimiento" to stringToTimestamp(tv_fechaNacimiento.text.toString()),
                    "ubicacion" to ubicacion,
                    "correo" to tv_correo.text.toString()
                )

                userRef.whereEqualTo("id", id)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val usuarioRef = document.reference
                            usuarioRef.update(usuarioActualizado as Map<String, Any>)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Usuario actualizado correctamente.")
                                    Toast.makeText(this, "Se actualizó correctamente el usuario", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error al actualizar usuario: $e")
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error al obtener usuarios: $e")
                    }
                var intent = Intent(this, CuentaActivity::class.java)
                intent.putExtra("name", "$nombre $apellidos")
                intent.putExtra("photo",photoURI)
                intent.putExtra("id",id)
                startActivity(intent)

            }
        }
        btnBack.setOnClickListener { finish() }

    }
    /**
     * Agregue valdaciones numero de telefono
     */
    private fun validaciones() : Boolean {
        val numeroTelefono: EditText= findViewById(R.id.tv_telefono)
        numeroTelefono.length()==10

        val fechaNacimiento : EditText = findViewById(R.id.tv_misDatosFechaNacimiento)
        val cal = Calendar.getInstance()
        val currentDate = cal.time
        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val datee = null

        try{
            val datee = fecha.parse(fechaNacimiento.text.toString())
        }catch(e: Exception){
            Toast.makeText(this, "Selecciona una fecha válida!", Toast.LENGTH_LONG).show()
            return false
        }

        if(ubicacion=="Seleccione una colonia"){//AQUIIIIIIII
            Toast.makeText(this, "Selecciona una colonia", Toast.LENGTH_LONG).show()
            return false
        }

        if(numeroTelefono.text.toString().length <= 9 && numeroTelefono.text.toString().length >=1){
            Toast.makeText(this, "Favor de ingresar los 10 digitos", Toast.LENGTH_LONG).show()
            return false
        }


        if (datee?.compareTo(currentDate) == 1) {
            Toast.makeText(this, "No puedes ingresar una fecha futura", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
    private fun mostrarDatePicker() {
        val editTextFecha = findViewById<EditText>(R.id.tv_misDatosFechaNacimiento)
        val fechaActual = Calendar.getInstance()

        val fechaTexto = editTextFecha.text.toString()

        // Verificar si el EditText no está vacío
        if (fechaTexto.isNotEmpty()) {
            val fecha = stringToDate(fechaTexto)

            // Verificar si la fecha es válida
            if (fecha != null) {
                fechaActual.time = fecha
            }
        }

        val datePickerDialog = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
            val fecha = "$dayOfMonth/${monthOfYear + 1}/$year"
            editTextFecha.setText(fecha)
        }, fechaActual.get(Calendar.YEAR), fechaActual.get(Calendar.MONTH), fechaActual.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }

    private fun stringToDate(dateString: String): Date? {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            formatter.parse(dateString)
        } catch (e: ParseException) {
            null
        }
    }
    fun stringToTimestamp(dateString: String): Timestamp? {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = formatter.parse(dateString)
        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_MONTH, +1)
        }
        return date?.let { Timestamp(calendar.timeInMillis / 1000, 0) }
    }
    private fun formatGeoPoint(latitude: Double, longitude: Double): String {
        val latStr = "${kotlin.math.abs(latitude)}° ${if (latitude >= 0) "N" else "S"}"
        val lonStr = "${kotlin.math.abs(longitude)}° ${if (longitude >= 0) "E" else "W"}"
        return "[$latStr, $lonStr]"
    }

    fun secondsToDate(seconds: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.timeInMillis = seconds * 1000 // Multiplicamos por 1000 para convertir segundos a milisegundos
        cal.add(Calendar.DAY_OF_MONTH, -1) // Restamos un día
        val date = cal.time
        return formatter.format(date)
    }

    private fun llenarSpinner(): Array<String> {
        val categoriasColonias = arrayOf("Seleccione una colonia",
            "Agronomos",
            "Alameda del Cedro",
            "Alameda del Cedro II",
            "Algodones",
            "Alta California",
            "Altar Residencial",
            "Amanecer 1",
            "Amanecer 2",
            "Ampliación Miguel Alemán",
            "Ampliación Miravalle",
            "Aves Del Castillo",
            "Bellavista",
            "Benito Juárez",
            "Bosque Del Nainari",
            "Bugambilias",
            "Cajeme",
            "Campanario",
            "Campestre",
            "Campestre 2da. Ampliación",
            "Casa Blanca",
            "Casa Blanca Ampliación II",
            "Casa Real",
            "Cd. Obregón (Ciudad Obregón)",
            "Central de Abastos",
            "Chapultepec",
            "Chihuahua",
            "Cincuentenario",
            "Ciudad Obregón Centro (Fundo Legal)",
            "Colinas del Yaqui",
            "Constitución",
            "Cortinas 1ra. Sección",
            "Cortinas 2da. Sección",
            "Cortinas 3ra. Sección",
            "Cortinas 4ta. Sección",
            "Cuauhtémoc Cárdenas",
            "Cuauhtémoc (Urbanizable 6)",
            "Cumuripa",
            "Del Bosque",
            "Del Lago",
            "Del Valle",
            "Ejidatarios",
            "Ejido Cajeme",
            "Electricista",
            "El Paraíso",
            "El Roble",
            "El Rodeo",
            "Esperanza Tiznada",
            "Faustino Félix Serna",
            "Fovissste",
            "Fovissste 2",
            "Fovissste 3",
            "Francisco Eusebio Kino",
            "Franja Comercial 300",
            "Fuentes Del Bosque",
            "Galeana",
            "Girasoles",
            "Granjas FOVISSSTE Norte (Codornices)",
            "Hacienda Del Sol",
            "Hacienda Real",
            "Haciendas El Rosario",
            "Haciendas San Francisco",
            "Haciendas San Miguel",
            "Herradura",
            "Hidalgo",
            "Infonavit ",
            "ISSSTESON",
            "Jardines Del Valle",
            "Ladrillera",
            "La Florida",
            "La Joya Villa California IV",
            "La Misión",
            "La Reforma",
            "Las Arboledas",
            "Las Brisas",
            "Las Campanas",
            "Las Espigas",
            "Las Flores",
            "Las Fuentes",
            "Las Fuentes II",
            "Las Haciendas",
            "Las Misiones",
            "Las Palmas",
            "Las Puertas",
            "Las Torres",
            "Lázaro Mercado",
            "Libertad",
            "Linda Vista",
            "Los Álamos 1",
            "Los Álamos 2",
            "Los Alisos",
            "Los Angeles",
            "Los Arcos",
            "Los Encinos",
            "Los Encinos II",
            "Los Olivos",
            "Los Patios",
            "Los Portales",
            "Los Presidentes",
            "Los Sauces",
            "Luis Donaldo Colosio",
            "Luis Echeverría Álvarez (Álvaro Obregón)",
            "Manlio Fabio Beltrones",
            "Matías Mendez",
            "Maximiliano Rubio López",
            "México",
            "Mirasierra",
            "Mirasoles",
            "Miravalle",
            "Misión del Real",
            "Misión Del Sol",
            "Misioneros",
            "Misión San Javier",
            "Misión San Rafael",
            "Montecarlo",
            "Morelos",
            "Multifamiliares IMSS",
            "Municipio Libre",
            "Nainari Del Yaqui",
            "Noroeste",
            "Nueva Galicia",
            "Nueva Palmira",
            "Nuevo Amanecer",
            "Nuevo Cajeme",
            "Nuevo Real del Norte",
            "Ostimuri",
            "Otancahui",
            "Palmar",
            "Palma Real",
            "París",
            "Parque Industrial",
            "Parque Tecnológico",
            "Paseo Alameda",
            "Pedregal",
            "Pioneros",
            "Pioneros de Cajeme",
            "Posada del Sol",
            "Pradera Bonita",
            "Prados de La Laguna",
            "Prados Del Tepeyac",
            "Primavera",
            "Primero de Mayo",
            "Privada de La Laguna",
            "Puente Real",
            "Quinta Diaz",
            "Quinta Real",
            "Real Campestre",
            "Real Del Arco",
            "Real Del Bosque",
            "Real Del Sol",
            "Real del Sol Ampliación",
            "Real Del Valle",
            "Real de Sabinos",
            "Real de Sevilla",
            "Rincón Del Valle",
            "Robles Del Castillo",
            "Russo Vogel",
            "San Anselmo",
            "San Antonio",
            "San Juan Capistrano",
            "Santa Anita",
            "Santa Fe",
            "Sierra Vista",
            "Sochiloa",
            "Sonora",
            "Sóstenes Valenzuela",
            "Torres de París",
            "Ultratec Aves",
            "Urbanizable 2",
            "Urbanizable 3",
            "Urbanizable 4",
            "Urbanizable 5",
            "Urbanizable 6 (Cuauhtémoc) Ampliación",
            "Urbanizable 7",
            "Urbanizable I",
            "Valle del Sol",
            "Valle Dorado",
            "Valle Verde",
            "Villa Alegre",
            "Villa Aurora",
            "Villa California",
            "Villa California 2",
            "Villa del Nainari",
            "Villa del Rey Sección Colonial",
            "Villa Del Tetabiate",
            "Villa Florencia",
            "Villa Fontana",
            "Villa Guadalupe",
            "Villa Itson",
            "Villa Mezquite",
            "Villa Satélite",
            "Villas de Cortés",
            "Villas del Campestre",
            "Villas del Palmar",
            "Villas del Real",
            "Villas Del Rey",
            "Villas Del Sol",
            "Villas de Trigo",
            "Vista Hermosa",
            "Zona Norte",
            "Zona Norte Comercial",
            )

        return categoriasColonias;
    }
}