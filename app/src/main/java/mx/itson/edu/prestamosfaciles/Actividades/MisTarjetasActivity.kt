package mx.itson.edu.prestamosfaciles.Actividades

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import mx.itson.edu.prestamosfaciles.Entidades.Producto
import mx.itson.edu.prestamosfaciles.Entidades.Tarjeta
import mx.itson.edu.prestamosfaciles.R
import java.text.SimpleDateFormat
import java.util.*


class MisTarjetasActivity : AppCompatActivity() {

    var adapter: TarjetaAdapter? = null
    var tarjetas = ArrayList<Tarjeta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mis_tarjetas)

        val bundle = intent.extras

        cargarTarjetas()
        val gridview: GridView = findViewById(R.id.id_grid)
        if (bundle != null) {
            val producto = intent.getSerializableExtra("producto") as Producto
            val usuario = bundle.getString("id")
            adapter = TarjetaAdapter(tarjetas,this,producto.precio)
            gridview.adapter = adapter
            intent.putExtra("producto",producto)
        }

        val btnMas: Button = findViewById(R.id.btn_agregar_tarjeta)

        btnMas.setOnClickListener{
            var intent = Intent(this, AgregarTarjetaActivity::class.java)
            val usuario = bundle?.getString("id")
            intent.putExtra("id",usuario)
            startActivity(intent)
        }

    }
    fun cargarTarjetas(){
        // Aquí se cargarán las tarjetas, pero por lo mientras, se cargan así xD

    }

    class TarjetaAdapter : BaseAdapter{
        var tarjetas = ArrayList<Tarjeta>()
        var context: Context? = null
        var precio: Double = 0.0
        constructor(tarjetas: ArrayList<Tarjeta>, context: Context?, precio: Double) : super() {
            this.tarjetas = tarjetas
            this.context = context
            this.precio = precio
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

            cuatroDigitos.setText("XXXX "+tarjeta.numTarjeta.substring(12,16))
            vencimiento.setText(getFormatoFecha(tarjeta.mesCaducidad + "/"+tarjeta.anioCaducidad))
            layout.setOnClickListener{
                if(precio != 0.0){
                    val intento = Intent(context, PagoActivity::class.java)
                    val intent = (context as MisTarjetasActivity).intent
                    val producto = intent.getSerializableExtra("producto") as Producto
                    intento.putExtra("tarjeta",tarjeta)
                    intento.putExtra("producto",producto)
                    context!!.startActivity(intento)
                }
            }
            return vista
        }

        fun getFormatoFecha(fecha: String?) : String{
            val formatoActual = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
            // Convertimos el String a un objeto Date
            val fecha = formatoActual.parse(fecha)

            // Creamos una segunda instancia de SimpleDateFormat con el formato deseado
            val formatoDeseado = SimpleDateFormat("MM/yy", Locale.US)

            // Formateamos la fecha en el formato deseado
            val fechaFormateada = formatoDeseado.format(fecha)
            return fechaFormateada
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