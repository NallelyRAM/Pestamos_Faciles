package mx.itson.edu.prestamosfaciles

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*
import kotlin.collections.ArrayList

class PrincipalActivity : AppCompatActivity() {

    var adapter: ProductoAdapter? = null
    var productos = ArrayList<Producto>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal)



        cargarProductos()
        val gridview: GridView = findViewById(R.id.id_grid)
        adapter = ProductoAdapter(productos,this)
        gridview.adapter = adapter

    }

    fun cargarProductos(){

        // Aquí se obtienen los productos, pero por mientras, se hace así pa :v

        productos.add(Producto("Pala","Bien chila pa cavar", R.drawable.pala,2,"Jardinería",549.5,
                                "Colonia México 158, Cd Obregón","Bueno","Ninguna"))

        productos.add(Producto("Pico","Pa q piques diamantes", R.drawable.pico,5,"Minería",805.99,
            "Colonia Miravalle 258, Cd Obregón","Excelente","Ninguna"))

        productos.add(Producto("Espada","Pa matar mas rapido", R.drawable.espada,10,"Armamento",1249.99,
            "Colonia Primero de Mayo, Cd Obregón","Medio","No matar endermans"))

    }

    class ProductoAdapter : BaseAdapter{
        var productos = ArrayList<Producto>()
        var context: Context? = null

        constructor(productos: ArrayList<Producto>, context: Context?) : super() {
            this.productos = productos
            this.context = context
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
            imagen.setImageResource(producto.imagen)

            layoutClick.setOnClickListener{
                val intento = Intent(context,SeleccionProducto::class.java)
                intento.putExtra("nombre",producto.nombre)
                intento.putExtra("imagen",producto.imagen)
                intento.putExtra("precio",producto.precio)
                intento.putExtra("descripcion",producto.descripcion)
                context!!.startActivity(intento)
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

            var intent: Intent = Intent(this,CuentaActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("email", email)
            intent.putExtra("id", id)
            intent.putExtra("photo", photoURI)

            startActivity(intent)
        } else {
            Toast.makeText(this, "Ocurrió un error al cargar el usuario", Toast.LENGTH_SHORT).show()
        }


    }
}