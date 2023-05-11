package mx.itson.edu.prestamosfaciles.Actividades

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import mx.itson.edu.prestamosfaciles.R

class PrivacidadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privacidad)

        val btn_back: Button = findViewById(R.id.btn_back)

        btn_back.setOnClickListener { finish() }

    }
}