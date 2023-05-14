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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.FirebaseFirestore
import mx.itson.edu.prestamosfaciles.Entidades.Renta
import mx.itson.edu.prestamosfaciles.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistorialRentadoresActivity : AppCompatActivity() {

    private val rentasRef = FirebaseFirestore.getInstance().collection("rentas")
    private val arrayMisRentas: ArrayList<Renta> = arrayListOf()

    var idUsuario: String? = null
    var correo: String? = null
    var nombre: String? = null

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_rentadores)

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        val bundle = intent.extras

        val btn_back: Button = findViewById(R.id.btn_back)

        if(bundle != null){
            nombre = bundle.getString("name")
            correo = bundle.getString("email")
            idUsuario = bundle.getString("id")
        }


        swipeRefreshLayout.setOnRefreshListener {
            // Llamamos a la función que se encarga de actualizar los datos
            arrayMisRentas.clear()
            cargarHistorialRentadores(idUsuario)

        }



        btn_back.setOnClickListener { finish() }

        val helpButton: ImageView = findViewById(R.id.helpButton)
        helpButton.setOnClickListener {
            Toast.makeText(this, "Aquí encontrarás información sobre los productos que tienes que han sido rentados por usuarios.", Toast.LENGTH_LONG).show()
        }


    }
    private fun cargarHistorialRentadores(id: String?){
        arrayMisRentas.clear()
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.isRefreshing = true
        rentasRef
            .whereEqualTo("producto.idVendedor", id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val renta = document.toObject(Renta::class.java)
                    arrayMisRentas.add(renta)
                }

                val adapter = RentaAdapter(arrayMisRentas, this)
                val gridview: GridView = findViewById(R.id.id_gridMisPrestamos)
                gridview.adapter = adapter
                swipeRefreshLayout.isRefreshing = false
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al buscar productos por nombre: $exception")
            }
    }
    class RentaAdapter : BaseAdapter {

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
            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var vista = inflator.inflate(R.layout.rentador,null)


            val nombre = vista.findViewById(R.id.tv_nombre) as TextView
            val nombreAlquiler = vista.findViewById<TextView>(R.id.tv_nombre_alquiler)
            val fechaVencimiento = vista.findViewById<TextView>(R.id.tv_fecha_vencimiento)
            val totalRenta = vista.findViewById<TextView>(R.id.tv_precio_total)

            nombre.text = renta.usuario.nombre
            nombreAlquiler.text = renta.producto.nombre
            fechaVencimiento.text = timestampToDate(renta.fechaVencimiento)
            totalRenta.text = "$"+renta.totalRenta
            var layout = vista.findViewById<LinearLayout>(R.id.layout_rentador)

            layout.setOnClickListener{
                val intento = Intent(context, RentadorDetallesActivity::class.java)
                intento.putExtra("idRenta", renta.id)
                intento.putExtra("seleccion",1)
                context!!.startActivity(intento)
            }
            return vista
        }

        fun timestampToDate(timestamp: com.google.firebase.Timestamp): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val date = Date(timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000)
            return dateFormat.format(date)
        }
    }

    override fun onResume() {
        super.onResume()
        // Aquí ejecuta la función que deseas
        cargarHistorialRentadores(idUsuario)
    }
}