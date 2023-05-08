package mx.itson.edu.prestamosfaciles.Actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import mx.itson.edu.prestamosfaciles.R

class HistorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)



    }

    fun btnMiPerfil(view: View){
        var intent: Intent = Intent(this, CuentaActivity::class.java)
        startActivity(intent)
    }
    fun btnHome(view: View){
        var intent: Intent = Intent(this, PrincipalActivity::class.java)
        startActivity(intent)
    }
}