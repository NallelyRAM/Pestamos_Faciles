package mx.itson.edu.prestamosfaciles

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class UbicacionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ubicacion)
    }

    fun btnCondiciones(view: View){
        var intent: Intent = Intent(this,CondicionesActivity::class.java)
        startActivity(intent)
    }

}