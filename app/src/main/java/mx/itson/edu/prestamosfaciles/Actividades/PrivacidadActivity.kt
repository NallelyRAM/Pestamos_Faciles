package mx.itson.edu.prestamosfaciles.Actividades

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import mx.itson.edu.prestamosfaciles.R

class PrivacidadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privacidad)



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