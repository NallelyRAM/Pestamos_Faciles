package mx.itson.edu.prestamosfaciles.Actividades

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
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
    var ubicacion: GeoPoint? = null
    var id = ""
    var photoURI: Uri? = null

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datos)

        val bundle = intent.extras

        val tv_nombre: TextView = findViewById(R.id.tv_misDatosNombre)
        val tv_apellidos: TextView = findViewById(R.id.tv_misDatosApellidos)
        val tv_fechaNacimiento: TextView = findViewById(R.id.tv_misDatosFechaNacimiento)
        val tv_telefono: TextView = findViewById(R.id.tv_telefono)
        val tv_correo: TextView = findViewById(R.id.tv_Correo)
        val tv_ubicacion: TextView = findViewById(R.id.tv_misDatosUbicacion)

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
                        ubicacion = data["ubicacion"] as? GeoPoint
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
                    tv_ubicacion.text = ubicacion?.let { formatGeoPoint(it.latitude,it.longitude) } ?: ""

                }
            }.addOnFailureListener { exception ->
                // maneja la excepción si no se puede completar la consulta
            }
        }

        val btnFinalizar = findViewById<TextView>(R.id.btn_inicioFinalizar)
        btnFinalizar.setOnClickListener{

            val usuarioActualizado = hashMapOf(
                "nombre" to tv_nombre.text.toString(),
                "apellido" to tv_apellidos.text.toString(),
                "telefono" to tv_telefono.text.toString(),
                "fechaNacimiento" to stringToTimestamp(tv_fechaNacimiento.text.toString()),
                "ubicacion" to stringToGeoPoint(tv_ubicacion.text.toString()),
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

        btnBack.setOnClickListener { finish() }

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

    private fun stringToGeoPoint(s: String): GeoPoint {
        val regex = Regex("""\[(\d+(?:\.\d+)?)° ([NS]), (\d+(?:\.\d+)?)° ([EW])\]""")
        val matchResult = regex.find(s) ?: throw IllegalArgumentException("Invalid format")
        val (latStr, latDir, lonStr, lonDir) = matchResult.destructured
        val lat = latStr.toDouble() * if (latDir == "N") 1 else -1
        val lon = lonStr.toDouble() * if (lonDir == "E") 1 else -1
        return GeoPoint(lat, lon)
    }

    fun secondsToDate(seconds: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.timeInMillis = seconds * 1000 // Multiplicamos por 1000 para convertir segundos a milisegundos
        cal.add(Calendar.DAY_OF_MONTH, -1) // Restamos un día
        val date = cal.time
        return formatter.format(date)
    }

}