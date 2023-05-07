package mx.itson.edu.prestamosfaciles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*


class AgregarProductoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

        val categoriasProductos = arrayOf("Seleccione una categoría","Electrónica", "Moda", "Hogar y jardín", "Belleza y cuidado personal", "Deportes y actividades al aire libre", "Juguetes y juegos", "Alimentos y bebidas", "Libros y medios")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriasProductos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner: Spinner = findViewById(R.id.sp_categoria_producto)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Item seleccionado
                val item = parent.getItemAtPosition(position) as String
                Toast.makeText(this@AgregarProductoActivity, "Seleccionaste: $item", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }


        val btn_back: Button = findViewById(R.id.btn_back)


        btn_back.setOnClickListener {
            onBackPressed()
        }

    }



}