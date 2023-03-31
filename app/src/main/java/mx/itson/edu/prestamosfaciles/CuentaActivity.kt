package mx.itson.edu.prestamosfaciles

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class CuentaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cuenta)

        val btnMisDatos = findViewById<TextView>(R.id.tv_misDatos)
        val btnMiHistorial = findViewById<TextView>(R.id.tv_historial)
        val btnMisTarjetas = findViewById<TextView>(R.id.tv_misTarjetas)
        val btnMiPrivacidad = findViewById<TextView>(R.id.tv_privacidad)
        val btnCerrarMiSesion = findViewById<TextView>(R.id.tv_cerrarSesion)

        btnMisDatos.setOnClickListener{
            var intent: Intent = Intent(this,DatosPersonalesActivity::class.java)
            startActivity(intent)
        }

        btnMiHistorial.setOnClickListener{
            var intent: Intent = Intent(this,HistorialActivity::class.java)
            startActivity(intent)
        }

        btnMisTarjetas.setOnClickListener{
            var intent: Intent = Intent(this,MisTarjetasActivity::class.java)
            intent.putExtra("seleccion",1)
            startActivity(intent)
        }

        btnMiPrivacidad.setOnClickListener{
            var intent: Intent = Intent(this,PrivacidadActivity::class.java)
            startActivity(intent)
        }

        btnCerrarMiSesion.setOnClickListener{
            cerrarSesion()
        }

    }

    fun btnHome(view: View){
        var intent: Intent = Intent(this,PrincipalActivity::class.java)
        startActivity(intent)
    }

    fun cerrarSesion() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Estás seguro de cerrar sesión?")
        builder.setPositiveButton("Sí") { _, _ ->
            // Si desea cerrar sesión
            val intent = Intent(this, InicioActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No") { _, _ ->
            // Si no desea cerrar sesión
        }
        val dialog = builder.create()
        dialog.show()
    }
}