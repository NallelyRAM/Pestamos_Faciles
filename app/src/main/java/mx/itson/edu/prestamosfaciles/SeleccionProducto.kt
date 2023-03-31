package mx.itson.edu.prestamosfaciles

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class SeleccionProducto : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccion_producto)

        val iv_producto: ImageView = findViewById(R.id.iv_producto_seleccionado)
        val tv_nombreProducto: TextView = findViewById(R.id.tv_nombreProducto)
        val tv_precio: TextView = findViewById(R.id.tv_precio_seleccionProducto)
        val tv_descripcion: TextView = findViewById(R.id.tv_descripcion_seleccion_producto)

        val bundle = intent.extras

        if(bundle != null){
            iv_producto.setImageResource(bundle.getInt("imagen"))
            tv_nombreProducto.text = bundle.getString("nombre")
            tv_precio.text = "$${bundle.getDouble("precio")}"
            tv_descripcion.text = bundle.getString("descripcion")
        }

        var btnDetalles: Button = findViewById(R.id.btn_detalles)

        btnDetalles.setOnClickListener{
            if(bundle != null){
                val intento = Intent(this,CondicionesActivity::class.java)
                intento.putExtra("nombre",bundle.getString("nombre"))
                intento.putExtra("imagen",bundle.getInt("imagen"))
                intento.putExtra("precio",bundle.getDouble("precio"))
                intento.putExtra("descripcion",bundle.getString("descripcion"))
                this!!.startActivity(intento)
            }
        }
    }
}
