package mx.itson.edu.prestamosfaciles.Actividades

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mx.itson.edu.prestamosfaciles.R
import java.text.SimpleDateFormat
import java.util.*

class PagoActivity : AppCompatActivity() {

    var IVA: Double = 0.16

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pago)

        val tv_fechaVencimiento: TextView = findViewById(R.id.tv_fechaDeVencimiento)
        val tv_numeroTarjeta: TextView = findViewById(R.id.tv_numeroTarjeta)
        val tv_cvv: TextView = findViewById(R.id.tv_numeroCCV)
        val tv_emisor: TextView = findViewById(R.id.tv_emisor)

        val tv_subtotal: TextView = findViewById(R.id.tv_cantidadSubtotal)
        val tv_iva: TextView = findViewById(R.id.tv_cantidadIVA)
        val tv_total: TextView = findViewById(R.id.tv_cantidadTotal)

        val btn_pagar: Button = findViewById(R.id.btn_pagar)

        val bundle = intent.extras

        if(bundle != null){
            var subtotal = bundle.getDouble("precio")
            var numeroTarjeta = bundle.getString("numTarjeta")
            var fechaVencimiento = getFormatoFecha(bundle.getString("fechaVencimiento"))

            var CVV = bundle.getString("CVV")
            var emisor = bundle.getString("emisor")

            var IVA_actual = subtotal * IVA
            var total = subtotal + IVA_actual

            tv_subtotal.text = String.format("$%,.2f", subtotal)
            tv_iva.text = String.format("$%,.2f", IVA_actual)
            tv_total.text = String.format("$%,.2f", total)

            tv_numeroTarjeta.text = numeroTarjeta
            tv_fechaVencimiento.text = fechaVencimiento
            tv_cvv.text = CVV
            tv_emisor.text = emisor
        }

        btn_pagar.setOnClickListener{
            Toast.makeText(this, "Gracias por su compra! :D", Toast.LENGTH_SHORT).show()
            var intent: Intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
        }

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