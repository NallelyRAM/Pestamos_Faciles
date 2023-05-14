package mx.itson.edu.prestamosfaciles.Actividades

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.FirebaseFirestore
import mx.itson.edu.prestamosfaciles.Entidades.Producto
import mx.itson.edu.prestamosfaciles.Entidades.Tarjeta
import mx.itson.edu.prestamosfaciles.Entidades.User
import mx.itson.edu.prestamosfaciles.R
import java.util.*
import kotlin.collections.HashMap
class MisTarjetasActivity : AppCompatActivity() {

    private val userRef = FirebaseFirestore.getInstance().collection("usuarios")

    var adapter: TarjetaAdapter? = null
    var tarjetas = ArrayList<Tarjeta>()
    var bundle: Bundle? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mis_tarjetas)
        cargarTarjetas()
        bundle = intent.extras

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)

        swipeRefreshLayout.setOnRefreshListener {
            // Llamamos a la función que se encarga de actualizar los datos
            tarjetas.clear()
            cargarTarjetas()
            swipeRefreshLayout.isRefreshing = false
        }
        swipeRefreshLayout.isRefreshing = false



        val btnMas: Button = findViewById(R.id.btn_agregar_tarjeta)
        val btnBack: Button = findViewById(R.id.btn_back)

        btnMas.setOnClickListener{
            var intent = Intent(this, AgregarTarjetaActivity::class.java)
            val usuario = bundle?.getString("id")
            intent.putExtra("id",usuario)
            startActivity(intent)
        }

        btnBack.setOnClickListener { finish() }

        val gridview: GridView = findViewById(R.id.id_grid)

        gridview.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->

            // Obtenemos la tarjeta seleccionada por el usuario
            val selectedItem = parent.getItemAtPosition(position) as Tarjeta

            //Log.e(ContentValues.TAG, "ITEM SELECCIONADO CLASE::::::::::::::${selectedItem.javaClass}")

            // Crea un diálogo con las opciones de acción
            val dialog = AlertDialog.Builder(this)
                .setTitle("¿Qué deseas hacer?")
                .setItems(arrayOf("Actualizar", "Eliminar")) { _, which ->
                    when (which) {
                        0 -> {
                            // Actualizamos tarjeta
                            actualizarTarjeta(selectedItem)
                        }
                        1 -> {
                            // Eliminamos tarjeta
                            eliminarTarjeta(selectedItem)
                        }
                    }
                }
                .create()

            // Mostrar el diálogo
            dialog.show()

            // Devuelve verdadero para indicar que el evento ha sido manejado
            true
        }
    }
    private fun actualizarTarjeta(tarjeta: Tarjeta) {
        var intent = Intent(this, AgregarTarjetaActivity::class.java)
        val usuario = bundle?.getString("id")
        intent.putExtra("tarjeta",tarjeta)
        intent.putExtra("id",usuario)
        startActivity(intent)
    }
    private fun eliminarTarjeta(tarjeta: Tarjeta){

        val usuarioId = bundle?.getString("id") ?: return

        // Primero, obtén la referencia del documento que contiene las tarjetas
        userRef.whereEqualTo("id", usuarioId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userDocRef = document.reference

                    userDocRef.get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            val user = document.toObject(User::class.java)

                            // Busca la tarjeta que quieres eliminar
                            val numTarjetaAEliminar = tarjeta.numTarjeta// número de tarjeta a eliminar
                            val tarjetaAEliminar = user?.tarjetas?.find { it.numTarjeta == numTarjetaAEliminar }

                            // Si se encontró la tarjeta, elimínala del array
                            if (tarjetaAEliminar != null) {

                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("Confirmación")
                                builder.setMessage("¿Estás seguro de que quieres eliminar la tarjeta?")
                                builder.setPositiveButton("Sí") { _, _ ->
                                    user.tarjetas?.remove(tarjetaAEliminar)

                                    // Actualiza el documento con el array actualizado
                                    userDocRef.set(user!!)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Tarjeta eliminada correctamente.")
                                            Toast.makeText(this, "Se eliminó correctamente la tarjeta", Toast.LENGTH_LONG).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(TAG, "Error al eliminar tarjeta: $e")
                                            Toast.makeText(this, "Ocurrió un error inesperado", Toast.LENGTH_LONG).show()
                                        }
                                }
                                builder.setNegativeButton("No") { _, _ ->
                                    // Acción a realizar si el usuario cancela
                                }
                                val dialog = builder.create()
                                dialog.show()


                            } else {
                                Log.d(TAG, "No se encontró la tarjeta a eliminar.")
                            }
                        } else {
                            Log.d(TAG, "No se encontró el documento del usuario.")
                        }
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al obtener el usuario: $exception")
            }
    }
    fun cargarTarjetas(){
        var producto: Producto? = null
        try{
            producto = intent.getSerializableExtra("producto") as Producto
        }catch(e: Exception){

        }
        val bundle = intent.extras
        val id = bundle?.getString("id")
        val query = userRef.whereEqualTo("id", id)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                for (document in querySnapshot.documents) {
                    val data = document.data
                    val arrayData = data?.get("tarjetas") as ArrayList<*>

                    if(arrayData != null && arrayData.isNotEmpty()){
                        for (tarjetaMap in arrayData) {
                            val hashMapObject = tarjetaMap as HashMap<String, String>

                            val tarjeta = Tarjeta(
                                hashMapObject["numTarjeta"] as String,
                                hashMapObject["mesCaducidad"] as String,
                                hashMapObject["anioCaducidad"] as String,
                                hashMapObject["codigoPostal"] as String,
                                hashMapObject["nombreTitular"] as String,
                                hashMapObject["apellidoTitular"] as String,
                                hashMapObject["cvv"] as String,
                                hashMapObject["emisor"] as String
                            )
                            tarjetas.add(tarjeta)
                        }
                    } else {
                        Toast.makeText(this, "Agregue una tarjeta para rentar! :)", Toast.LENGTH_LONG).show()
                        break
                    }
                }
            } else {
                // No se encontraron documentos que coincidan con la consulta

            }

            adapter = TarjetaAdapter(tarjetas, this, producto,id)
            val gridview: GridView = findViewById(R.id.id_grid)
            gridview.adapter = adapter

        }.addOnFailureListener { exception ->
            // Manejar el error aquí
        }
    }
    class TarjetaAdapter : BaseAdapter{
        var tarjetas = ArrayList<Tarjeta>()
        var context: Context? = null
        var producto: Producto? = null
        var idUsuario: String? = null

        constructor(tarjetas: ArrayList<Tarjeta>, context: Context?, producto: Producto?, idUsuario: String?) : super() {
            this.tarjetas = tarjetas
            this.context = context
            this.producto = producto
            this.idUsuario = idUsuario
        }

        override fun getCount(): Int {
            return tarjetas.size
        }

        override fun getItem(position: Int): Any {
            return tarjetas[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        @SuppressLint("MissingInflatedId")
        override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {
            var tarjeta = tarjetas[position]
            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var vista = inflator.inflate(R.layout.detalle_tarjeta,null)


            var cuatroDigitos = vista.findViewById(R.id.tv_detalle_terminaEn) as TextView
            var vencimiento = vista.findViewById<TextView>(R.id.tv_detalle_venceEn)
            var layout = vista.findViewById<LinearLayout>(R.id.layout_tarjeta)

            cuatroDigitos.setText("XXXX "+tarjeta.numTarjeta.takeLast(4))
            vencimiento.setText(tarjeta.mesCaducidad + "/"+tarjeta.anioCaducidad)
            layout.setOnClickListener{
                if(producto != null){
                    val intento = Intent(context, PagoActivity::class.java)
                    intento.putExtra("tarjeta",tarjeta)
                    intento.putExtra("producto",producto)
                    intento.putExtra("id",idUsuario)
                    context!!.startActivity(intento)
                }
            }
            return vista
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