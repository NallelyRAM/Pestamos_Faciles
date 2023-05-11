package mx.itson.edu.prestamosfaciles.Actividades

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import mx.itson.edu.prestamosfaciles.Entidades.Producto
import mx.itson.edu.prestamosfaciles.Entidades.Tarjeta
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
        val tv_nombreCompleto: TextView = findViewById(R.id.tv_nombreCompletoPropietario)

        val tv_subtotal: TextView = findViewById(R.id.tv_cantidadSubtotal)
        val tv_iva: TextView = findViewById(R.id.tv_cantidadIVA)
        val tv_total: TextView = findViewById(R.id.tv_cantidadTotal)

        val btn_pagar: Button = findViewById(R.id.btn_pagar)
        val btn_back: Button = findViewById(R.id.btn_back)

        val bundle = intent.extras

        if(bundle != null){
            val producto = intent.getSerializableExtra("producto") as Producto
            val tarjeta = intent.getSerializableExtra("tarjeta") as Tarjeta

            var subtotal = producto.precio
            var numeroTarjeta = tarjeta.numTarjeta
            var fechaVencimiento = tarjeta.mesCaducidad + "/"+tarjeta.anioCaducidad

            var CVV = tarjeta.CVV
            var emisor = tarjeta.emisor

            var IVA_actual = subtotal * IVA
            var total = subtotal + IVA_actual

            var iv_producto: ImageView = findViewById(R.id.imagen_producto)

            tv_subtotal.text = String.format("$%,.2f", subtotal)
            tv_iva.text = String.format("$%,.2f", IVA_actual)
            tv_total.text = String.format("$%,.2f", total)

            tv_numeroTarjeta.text = numeroTarjeta
            tv_fechaVencimiento.text = fechaVencimiento
            tv_cvv.text = CVV
            tv_emisor.text = emisor
            tv_nombreCompleto.text = tarjeta.nombreTitular + " " + tarjeta.apellidoTitular

            Glide.with(this)
                .load(producto.imagen)
                .into(iv_producto)


        }

        btn_pagar.setOnClickListener{
            Toast.makeText(this, "Gracias por su compra! :D", Toast.LENGTH_SHORT).show()
            var intent: Intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
        }

        btn_back.setOnClickListener { finish() }

    }
}