package mx.itson.edu.prestamosfaciles.Actividades

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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
        llenadoSpinners()

        val bundle = intent.extras

        val btn_back: Button = findViewById(R.id.btn_back)
        val btn_aceptar: Button = findViewById(R.id.btn_aceptar)

        btn_aceptar.setOnClickListener { guardarTarjetaAUsuario(bundle) }

        btn_back.setOnClickListener { finish() }

    }

    private fun guardarTarjetaAUsuario(bundle: Bundle?){
        val et_numeroTarjeta: EditText = findViewById(R.id.et_numero_tarjeta)
        val et_nombrePropietario: EditText = findViewById(R.id.et_nombre_tarjeta)
        val et_apellidosPropietario: EditText = findViewById(R.id.et_apellidos_tarjeta)
        val et_codigoSeguridad: EditText = findViewById(R.id.et_codigo_seguridad)
        val et_codigoPostal: EditText = findViewById(R.id.et_codigo_postal)

        val tarjeta = Tarjeta(
            numTarjeta = et_numeroTarjeta.text.toString(),
            mesCaducidad = mesCaducidadSeleccionado,
            anioCaducidad = anioCaducidadSeleccionado,
            codigoPostal = et_codigoPostal.text.toString(),
            nombreTitular = et_nombrePropietario.text.toString(),
            apellidoTitular = et_apellidosPropietario.text.toString(),
            CVV = et_codigoSeguridad.text.toString(),
            emisor = emisorSeleccionado
        )
        val id = bundle?.getString("id").toString()
        userRef.whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val usuario = document.toObject(User::class.java)
                    usuario.addTarjeta(tarjeta)
                    val usuarioRef = document.reference
                    usuarioRef.update(usuario.toMap())
                        .addOnSuccessListener {
                            Log.d(TAG, "Usuario actualizado correctamente.")
                            Toast.makeText(this, "Se agregó la tarjeta correctamente", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error al guardar la tarjeta: $e")
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun llenadoSpinners(){
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
}