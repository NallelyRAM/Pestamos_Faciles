package mx.itson.edu.prestamosfaciles.Actividades

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_rentas)

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        val bundle = intent.extras

        val btn_back: Button = findViewById(R.id.btn_back)

        if(bundle != null){
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

                //val adapter = PrincipalActivity.ProductoAdapter(arrayMisRentas, this, intent.extras,null)
                //val gridview: GridView = findViewById(R.id.id_gridMisPrestamos)
                //gridview.adapter = adapter

            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al buscar productos por nombre: $exception")
            }
    }
}