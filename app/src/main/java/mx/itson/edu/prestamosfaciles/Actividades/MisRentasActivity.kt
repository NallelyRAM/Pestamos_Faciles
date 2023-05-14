package mx.itson.edu.prestamosfaciles.Actividades

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import mx.itson.edu.prestamosfaciles.Entidades.Producto
import mx.itson.edu.prestamosfaciles.Entidades.Renta
import mx.itson.edu.prestamosfaciles.R

class MisRentasActivity : AppCompatActivity() {

    private val rentasRef = FirebaseFirestore.getInstance().collection("rentas")
    private val arrayMisRentas: ArrayList<Renta> = arrayListOf()

    var idUsuario: String? = null
    var correo: String? = null
    var nombre: String? = null

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_rentas)

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        val bundle = intent.extras

        val btn_back: Button = findViewById(R.id.btn_back)

        if (bundle != null) {
            nombre = bundle.getString("name")
            correo = bundle.getString("email")
            idUsuario = bundle.getString("id")
        }
        cargarMisRentas(idUsuario)

        swipeRefreshLayout.setOnRefreshListener {
            // Llamamos a la función que se encarga de actualizar los datos
            arrayMisRentas.clear()
            cargarMisRentas(idUsuario)
            swipeRefreshLayout.isRefreshing = false
        }

        swipeRefreshLayout.isRefreshing = false

        btn_back.setOnClickListener { finish() }

        val helpButton: ImageView = findViewById(R.id.helpButton)
        helpButton.setOnClickListener {
            Toast.makeText(
                this,
                "Aquí encontrarás información sobre los productos que estás rentando.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    class MisRentasAdapter : BaseAdapter {
        var rentas = java.util.ArrayList<Renta>()
        var context: Context? = null

        constructor(rentas: java.util.ArrayList<Renta>, context: Context?) : super() {
            this.rentas = rentas
            this.context = context
        }

        override fun getCount(): Int {
            return rentas.size
        }

        override fun getItem(position: Int): Any {
            return rentas[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("MissingInflatedId")
        override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {
            var renta = rentas[position]
            var inflator =
                context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var vista = inflator.inflate(R.layout.detalle_prestamo, null)


            var nombre = vista.findViewById(R.id.tv_detalleNombre) as TextView
            var precio = vista.findViewById<TextView>(R.id.tv_detallePrecio)
            var descripcion = vista.findViewById<TextView>(R.id.tv_detalleDescripcion)
            var imagen = vista.findViewById<ImageView>(R.id.iv_producto)
            var layoutClick = vista.findViewById<LinearLayout>(R.id.producto_completo)

            nombre.text = renta.producto.nombre
            precio.text = "$${renta.producto.precio}"
            descripcion.text = limitarString(renta.producto.descripcion)
            Glide.with(context!!)
                .load(renta.producto.imagen)
                .into(imagen)
            layoutClick.setOnClickListener{
                val intento = Intent(context, RentadorDetallesActivity::class.java)
                intento.putExtra("idRenta", renta.id)
                context!!.startActivity(intento)
            }
            return vista
        }
        private fun limitarString(texto: String): String? {
            var texto = texto
            val limite = 50
            val puntosSuspensivos = "...."
            if (texto.length > limite) {
                texto = texto.substring(0, limite - puntosSuspensivos.length) + puntosSuspensivos
            }
            return texto
        }

    }
    private fun cargarMisRentas(id: String?){
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.isRefreshing = true
        rentasRef
            .whereEqualTo("usuario.id", id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val renta = document.toObject(Renta::class.java)
                    arrayMisRentas.add(renta)
                }
                val adapter = MisRentasAdapter(arrayMisRentas,this)
                val gridview: GridView = findViewById(R.id.id_gridMisPrestamos)
                gridview.adapter = adapter

            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al buscar productos por nombre: $exception")
            }
    }
}