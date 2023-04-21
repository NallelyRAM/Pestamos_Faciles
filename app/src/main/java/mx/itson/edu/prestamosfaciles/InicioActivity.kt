package mx.itson.edu.prestamosfaciles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class InicioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio)

        var btnIniciar: Button =findViewById(R.id.btn_iniciar_sesion) as Button

        var intent: Intent =Intent(this,PrincipalActivity::class.java)

        btnIniciar.setOnClickListener{


            startActivity(intent)
        }

    }

}