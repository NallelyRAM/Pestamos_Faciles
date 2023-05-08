package mx.itson.edu.prestamosfaciles.Actividades

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import mx.itson.edu.prestamosfaciles.R


class AgregarTarjetaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_tarjeta)

        llenarDatos()

    }
    fun llenarDatos(){
        val tipos_tarjeta: MutableList<String> = ArrayList()
        tipos_tarjeta.add("Visa")
        tipos_tarjeta.add("Mastercard")

        val mes_caducidad: MutableList<String> = ArrayList()
        mes_caducidad.add ("01")
        mes_caducidad.add("02")
        mes_caducidad.add("03")
        mes_caducidad.add("04")
        mes_caducidad.add("05")
        mes_caducidad.add("06")
        mes_caducidad.add("07")
        mes_caducidad.add("08")
        mes_caducidad.add("09")
        mes_caducidad.add("10")
        mes_caducidad.add("11")
        mes_caducidad.add("12")
        val anio_caducidad: MutableList<String> = ArrayList()
        anio_caducidad.add("2023")
        anio_caducidad.add("2024")
        anio_caducidad.add("2025")
        anio_caducidad.add("2026")
        anio_caducidad.add("2027")
        anio_caducidad.add("2028")
        anio_caducidad.add("2029")
        anio_caducidad.add("2030")


        val adaptador_tarjeta: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipos_tarjeta)
        adaptador_tarjeta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val sp_tipo_tarjeta: Spinner = findViewById(R.id.sp_tipo_tarjeta)
        sp_tipo_tarjeta.setAdapter(adaptador_tarjeta)

        val adaptador_mes: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mes_caducidad)
        adaptador_tarjeta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val sp_mes_caducidad: Spinner = findViewById(R.id.sp_mes_caducidad)
        sp_mes_caducidad.setAdapter(adaptador_mes)

        val adaptador_anio: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, anio_caducidad)
        adaptador_tarjeta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val sp_anio_caducidad: Spinner = findViewById(R.id.sp_año_caducidad)
        sp_anio_caducidad.setAdapter(adaptador_anio)
    }
}