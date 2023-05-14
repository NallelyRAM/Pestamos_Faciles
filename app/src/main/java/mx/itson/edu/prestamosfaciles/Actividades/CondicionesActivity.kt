package mx.itson.edu.prestamosfaciles.Actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import mx.itson.edu.prestamosfaciles.Entidades.Producto
import mx.itson.edu.prestamosfaciles.R

class CondicionesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_condiciones)

        val btnSiguiente: TextView = findViewById(R.id.btn_condicionesSiguiente)

        val iv_producto: ImageView = findViewById(R.id.iv_productoCondiciones)
        val tv_nombreProducto: TextView = findViewById(R.id.tv_condicionesNombreProducto)
        val tv_descripcion: TextView = findViewById(R.id.tv_condicionesDescripcion)

        val bundle = intent.extras

        if(bundle != null){
            val producto = intent.getSerializableExtra("producto") as Producto
            Glide.with(this)
                .load(producto.imagen)
                .into(iv_producto)
            tv_nombreProducto.text = producto.nombre
            tv_descripcion.text = producto.descripcion
        }

        btnSiguiente.setOnClickListener{
            if(bundle != null){
                val producto = intent.getSerializableExtra("producto") as Producto
                val intento = Intent(this, MisTarjetasActivity::class.java)
                intento.putExtra("producto",producto)
                intento.putExtra("id",bundle.getString("id"))
                this!!.startActivity(intento)
            }
        }

    }
}