package mx.itson.edu.prestamosfaciles.Actividades

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import mx.itson.edu.prestamosfaciles.Entidades.Producto
import mx.itson.edu.prestamosfaciles.R
import java.util.*
import kotlin.collections.ArrayList

class PrincipalActivity : AppCompatActivity() {

    var adapter: ProductoAdapter? = null
    var productos = ArrayList<Producto>()
    val productosRef = FirebaseFirestore.getInstance().collection("productos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal)
        val btnAgregarProducto = findViewById<ImageView>(R.id.iv_navMenu_Principal_mas)
        btnAgregarProducto.setOnClickListener{goScreenAddProducts()}

        cargarProductos()

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)

        swipeRefreshLayout.setOnRefreshListener {
            // Llamamos a la función que se encarga de actualizar los datos
            productos.clear()
            cargarProductos()
            swipeRefreshLayout.isRefreshing = false
        }
        swipeRefreshLayout.isRefreshing = false
    }

    fun cargarProductos(){
        // Hacer la consulta de Firestore
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.isRefreshing = true

        productosRef.get().addOnSuccessListener { querySnapshot ->
            // Iterar sobre los documentos en la consulta
            for (document in querySnapshot.documents) {
                // Acceder a las propiedades del producto
                val id = document.getString("id").toString()
                val nombre = document.getString("nombre").toString()
                val descripcion = document.getString("descripcion").toString()
                val categoria = document.getString("categoria").toString()
                val precio = document.getDouble("precio").toString().toDouble()
                val ubicacion = document.getString("ubicacion").toString()
                val imagenRef = document.getString("imagen").toString()
                val idVendedor = document.getString("idVendedor").toString()

                productos.add(Producto(
                    id,
                    nombre,
                    descripcion,
                    imagenRef,
                    categoria,
                    precio,
                    ubicacion,
                    idVendedor
                ))

                adapter = ProductoAdapter(productos, this,intent.extras,1)
                val gridview: GridView = findViewById(R.id.id_grid)
                gridview.adapter = adapter

            }
            adapter?.notifyDataSetChanged()

        }.addOnFailureListener { e ->
            // Manejar el error
            Log.e("TAG", "Error al obtener los productos", e)
        }

    }
    class ProductoAdapter : BaseAdapter{
        var productos = ArrayList<Producto>()
        var context: Context? = null
        var bundle: Bundle? = null
        var seleccion: Int? = null

        constructor(productos: ArrayList<Producto>, context: Context?, bundle: Bundle?, seleccion: Int?) : super() {
            this.productos = productos
            this.context = context
            this.bundle = bundle
            this.seleccion = seleccion
        }

        override fun getCount(): Int {
            return productos.size
        }

        override fun getItem(position: Int): Any {
            return productos[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }


        @SuppressLint("MissingInflatedId")
        override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {
            productos.sortBy { it.nombre.uppercase() }
            var producto = productos[position]
            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var vista = inflator.inflate(R.layout.detalle_prestamo,null)


            var nombre = vista.findViewById(R.id.tv_detalleNombre) as TextView
            var precio = vista.findViewById<TextView>(R.id.tv_detallePrecio)
            var descripcion = vista.findViewById<TextView>(R.id.tv_detalleDescripcion)
            var imagen = vista.findViewById<ImageView>(R.id.iv_producto)
            var layoutClick = vista.findViewById<LinearLayout>(R.id.producto_completo)

            nombre.setText(producto.nombre)
            precio.setText("$${producto.precio}")
            descripcion.setText(producto.descripcion)
            Glide.with(context!!)
                .load(producto.imagen)
                .into(imagen)


            layoutClick.setOnClickListener{
                if(seleccion != null){
                    val intento = Intent(context, SeleccionProductoActivity::class.java)
                    intento.putExtra("producto",producto)
                    intento.putExtra("id", bundle?.getString("id"))
                    intento.putExtra("seleccion","0")
                    context!!.startActivity(intento)
                }
            }
            return vista

        }
    }

    fun btnMiPerfil(view: View){

        val bundle = intent.extras

        if(bundle != null) {
            val name = bundle.getString("name")
            val email = bundle.getString("email")
            val id = bundle.getString("id")
            val photoURI = bundle?.getParcelable<Uri>("photo")

            var intent = Intent(this, CuentaActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("email", email)
            intent.putExtra("id", id)
            intent.putExtra("photo", photoURI)

            startActivity(intent)
        } else {
            Toast.makeText(this, "Ocurrió un error al cargar el usuario", Toast.LENGTH_SHORT).show()
        }
    }

    fun goScreenAddProducts(){

        val bundle = intent.extras

        if(bundle != null) {
            var intent = Intent(this, AgregarProductoActivity::class.java)
            intent.putExtra("id", bundle.getString("id"))
            startActivity(intent)
        }

    }

}