package mx.itson.edu.prestamosfaciles.Actividades

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import mx.itson.edu.prestamosfaciles.Entidades.Producto
import mx.itson.edu.prestamosfaciles.R

class SeleccionProductoActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccion_producto)

        val iv_producto: ImageView = findViewById(R.id.iv_producto_seleccionado)
        val tv_nombreProducto: TextView = findViewById(R.id.tv_nombreProducto)
        val tv_precio: TextView = findViewById(R.id.tv_precio_seleccionProducto)
        val tv_descripcion: TextView = findViewById(R.id.tv_descripcion_seleccion_producto)
        val btn_back: Button = findViewById(R.id.btn_back)
        val tv_ubicacion: TextView = findViewById(R.id.tv_ubicacion)
        var btnDetalles: Button = findViewById(R.id.btn_detalles)

        val bundle = intent.extras

        if(bundle != null){
            val producto = intent.getSerializableExtra("producto") as Producto
            Glide.with(this)
                .load(producto.imagen)
                .into(iv_producto)

            tv_nombreProducto.text = producto.nombre
            tv_precio.text = "$${producto.precio}"
            tv_descripcion.text = producto.descripcion
            tv_ubicacion.text = "Ubicado en "+ producto.ubicacion

            val seleccion = bundle.getString("seleccion")

            if(seleccion == null){
                btnDetalles.visibility = View.INVISIBLE
            }
        }
        btnDetalles.setOnClickListener{
            if(bundle != null){
                val intento = Intent(this, CondicionesActivity::class.java)
                val producto = intent.getSerializableExtra("producto") as Producto
                val idUsuario = bundle.getString("id")
                intento.putExtra("producto",producto)
                intento.putExtra("id",idUsuario)
                this!!.startActivity(intento)
            }
        }

        btn_back.setOnClickListener { finish() }
    }
}
