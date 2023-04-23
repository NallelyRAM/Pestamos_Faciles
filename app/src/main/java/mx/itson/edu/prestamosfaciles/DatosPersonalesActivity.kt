package mx.itson.edu.prestamosfaciles

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.util.*

class DatosPersonalesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datos)

        val bundle = intent.extras


        var name: String? = ""
        var email: String? = ""
        var id: String? = ""
        var photoURI: Uri? = null


        if(bundle != null) {
            name = bundle.getString("name")
            email = bundle.getString("email")
            id = bundle.getString("id")
            photoURI = bundle?.getParcelable<Uri>("photo")

            val tv_nombre: TextView = findViewById(R.id.tv_misDatosNombre)
            val tv_apellidos: TextView = findViewById(R.id.tv_misDatosApellidos)
            val tv_correo: TextView = findViewById(R.id.tv_Correo)

            val partes = name?.split(" ")
            val nombre = partes?.get(0)
            val apellidos = partes?.get(1)

            tv_nombre.text = nombre
            tv_apellidos.text = apellidos
            tv_correo.text = email

        }

        val btnFinalizar = findViewById<TextView>(R.id.btn_inicioFinalizar)
        btnFinalizar.setOnClickListener{
            var intent: Intent = Intent(this,CuentaActivity::class.java)
            startActivity(intent)
        }

    }

    fun mostrarDatePicker(view: View) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
            val fecha = "$dayOfMonth/${monthOfYear + 1}/$year"
            val editTextFecha = findViewById<EditText>(R.id.tv_misDatosFechaNacimiento)
            editTextFecha.setText(fecha)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }

}