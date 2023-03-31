package mx.itson.edu.prestamosfaciles

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MisTarjetasActivity : AppCompatActivity() {

    var adapter: TarjetaAdapter? = null
    var tarjetas = ArrayList<Tarjeta>()

    var precio: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mis_tarjetas)

        val bundle = intent.extras

        cargarTarjetas()
        val gridview: GridView = findViewById(R.id.id_grid)
        if (bundle != null) {
            precio = bundle.getDouble("precio")
        }

        adapter = TarjetaAdapter(tarjetas,this,precio)

        gridview.adapter = adapter



    }
    fun cargarTarjetas(){

        // Aquí se cargarán las tarjetas, pero por lo mientras, se cargan así xD
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var dateString = "2025-04-01"
        var date = format.parse(dateString)

        tarjetas.add(Tarjeta("4152 5896 4785 1458","1458", date,"Brayan","589","VISA"))

        dateString = "2026-10-01"
        date = format.parse(dateString)
        tarjetas.add(Tarjeta("5874 1458 1025 8575","8575", date,"Juanito","102","Mastercard"))

        dateString = "2025-08-05"
        date = format.parse(dateString)
        tarjetas.add(Tarjeta("5814 1025 8965 0014","0014", date,"Pedrito","158","American Express"))

        dateString = "2028-12-25"
        date = format.parse(dateString)
        tarjetas.add(Tarjeta("4587 0125 3697 7845","7845", date,"Luisito","145","VISA"))

        dateString = "2024-03-15"
        date = format.parse(dateString)
        tarjetas.add(Tarjeta("1478 9896 1023 8747","8747", date,"Nallely","587","VISA"))

        dateString = "2023-10-14"
        date = format.parse(dateString)
        tarjetas.add(Tarjeta("1356 8965 7845 4523","4523", date,"Joaquin","025","American Express"))
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

            cuatroDigitos.setText("XXXX "+tarjeta.ultimosCuatroDigitos)
            vencimiento.setText(getFormatoFecha(tarjeta.fechaVencimiento.toString()))
            layout.setOnClickListener{
                if(precio != 0.0){
                    val intento = Intent(context,PagoActivity::class.java)

                    intento.putExtra("numTarjeta",tarjeta.numTarjeta)
                    intento.putExtra("fechaVencimiento",tarjeta.fechaVencimiento.toString())
                    intento.putExtra("CVV",tarjeta.CVV)
                    intento.putExtra("nombreTitular",tarjeta.nombreTitular)
                    intento.putExtra("emisor",tarjeta.emisor)
                    intento.putExtra("ultimosCuatroDigitor",tarjeta.ultimosCuatroDigitos)
                    intento.putExtra("precio",precio)
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
        var intent: Intent = Intent(this,CuentaActivity::class.java)
        startActivity(intent)
    }
    fun btnHome(view: View){
        var intent: Intent = Intent(this,PrincipalActivity::class.java)
        startActivity(intent)
    }
}