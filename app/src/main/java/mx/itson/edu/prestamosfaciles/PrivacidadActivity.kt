package mx.itson.edu.prestamosfaciles

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class PrivacidadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privacidad)



    }

    fun btnMiPerfil(view: View){
        var intent: Intent = Intent(this,CuentaActivity::class.java)
        startActivity(intent)
    }
    fun btnHome(view: View){
        var intent: Intent = Intent(this,PrincipalActivity::class.java)
        startActivity(intent)
    }
}