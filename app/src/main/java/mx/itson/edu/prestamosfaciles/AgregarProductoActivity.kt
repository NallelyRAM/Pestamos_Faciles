package mx.itson.edu.prestamosfaciles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView


class AgregarProductoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

        val btn_back: Button = findViewById(R.id.btn_back)


        btn_back.setOnClickListener {
            onBackPressed()
        }

    }



}