package mx.itson.edu.prestamosfaciles.Actividades

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import mx.itson.edu.prestamosfaciles.R
class UbicacionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ubicacion)
    }
    fun btnCondiciones(){
        var intent = Intent(this, CondicionesActivity::class.java)
        startActivity(intent)
    }
}