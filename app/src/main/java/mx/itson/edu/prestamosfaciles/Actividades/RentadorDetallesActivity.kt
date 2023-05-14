package mx.itson.edu.prestamosfaciles.Actividades

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import mx.itson.edu.prestamosfaciles.Entidades.Renta
import mx.itson.edu.prestamosfaciles.R
import java.text.SimpleDateFormat
import java.util.*


class RentadorDetallesActivity : AppCompatActivity() {

    private val rentasRef = FirebaseFirestore.getInstance().collection("rentas")
    var idRenta: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_rentador)

        val tv_usuario = findViewById<TextView>(R.id.tv_usuario)
        val tv_fecha_alquiler = findViewById<TextView>(R.id.tv_fecha_alquiler)
        val tv_fecha_vencimiento = findViewById<TextView>(R.id.tv_fecha_vencimiento)
        val tv_articulo = findViewById<TextView>(R.id.tv_articulo)
        val tv_total = findViewById<TextView>(R.id.tv_total)

        val iv_producto = findViewById<ImageView>(R.id.imagen_articulo)
        val tv_descripcion_articulo = findViewById<TextView>(R.id.tv_descripcion_articulo)
        val btn_productoEntregado = findViewById<Button>(R.id.btn_productoEntregado)


        val bundle = intent.extras

        if (bundle != null) {
            val seleccion = bundle.getInt("seleccion")
            if(seleccion != 0){
                btn_productoEntregado.visibility = View.VISIBLE
            }

            idRenta = bundle.getString("idRenta").toString()

            rentasRef.whereEqualTo("id", idRenta)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val renta = document.toObject(Renta::class.java)

                        if(renta != null){
                            tv_usuario.text = renta.usuario.nombre + " " + renta.usuario.apellido
                            tv_fecha_alquiler.text = timestampToDate(renta.fechaRenta)
                            tv_fecha_vencimiento.text = timestampToDate(renta.fechaVencimiento)
                            tv_articulo.text = renta.producto.nombre
                            tv_total.text = "$"+renta.totalRenta
                            tv_descripcion_articulo.text = renta.producto.descripcion

                            Glide.with(this)
                                .load(renta.producto.imagen)
                                .into(iv_producto)
                        }

                    }

                }
        }

        btn_productoEntregado.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro de que quieres marcar el producto como entregado? Está acción no es reversible.")
            builder.setPositiveButton("Sí") { _, _ ->
                marcarProductoEntregado(idRenta)
            }
            builder.setNegativeButton("No") { _, _ ->
                // Acción a realizar si el usuario cancela
            }
            val dialog = builder.create()
            dialog.show()
        }

        val btn_back = findViewById<Button>(R.id.btn_back)
        btn_back.setOnClickListener { finish() }
    }
    fun timestampToDate(timestamp: com.google.firebase.Timestamp): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = Date(timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000)
        return dateFormat.format(date)
    }

    fun marcarProductoEntregado(idRenta: String){
        rentasRef.whereEqualTo("id", idRenta)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // No se encontró ningún producto con el nombre dado
                    Toast.makeText(this, "No se encontró la renta a eliminar", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                for (document in querySnapshot) {
                    document.reference.delete().addOnSuccessListener {
                        Toast.makeText(this, "Renta marcada como entregada", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error al marcar como entregada", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            .addOnFailureListener { exception ->
                // Manejar errores
                Toast.makeText(this, "Error al marcar como entregada", Toast.LENGTH_SHORT).show()
            }
    }

}