package mx.itson.edu.prestamosfaciles

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class DatosPersonalesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datos)

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