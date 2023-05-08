package mx.itson.edu.prestamosfaciles.Actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import mx.itson.edu.prestamosfaciles.R

class CondicionesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_condiciones)

        val btnUbicacion: TextView = findViewById(R.id.btn_condicionesLocalizar)
        val btnSiguiente: TextView = findViewById(R.id.btn_condicionesSiguiente)

        val iv_producto: ImageView = findViewById(R.id.iv_productoCondiciones)
        val tv_nombreProducto: TextView = findViewById(R.id.tv_condicionesNombreProducto)
        val tv_descripcion: TextView = findViewById(R.id.tv_condicionesDescripcion)

        val bundle = intent.extras

        if(bundle != null){
            iv_producto.setImageResource(bundle.getInt("imagen"))
            tv_nombreProducto.text = bundle.getString("nombre")
            tv_descripcion.text = bundle.getString("descripcion")
        }

        btnUbicacion.setOnClickListener{
            var intent: Intent = Intent(this, UbicacionActivity::class.java)
            startActivity(intent)
        }

        btnSiguiente.setOnClickListener{
            if(bundle != null){
                var precio = bundle.getDouble("precio")
                val intento = Intent(this, MisTarjetasActivity::class.java)
                intento.putExtra("precio",precio)
                this!!.startActivity(intento)
            }
        }

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