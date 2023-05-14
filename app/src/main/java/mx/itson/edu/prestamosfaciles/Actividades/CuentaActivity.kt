package mx.itson.edu.prestamosfaciles.Actividades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import mx.itson.edu.prestamosfaciles.R

class CuentaActivity : AppCompatActivity() {


    var name: String? = ""
    var id: String? = ""
    var photoURI: Uri? = null
    var correo: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cuenta)

        val bundle = intent.extras

        if(bundle != null) {
            name = bundle.getString("name")
            id = bundle.getString("id")
            photoURI = bundle?.getParcelable<Uri>("photo")
            correo = bundle.getString("email")

            val tv_nombre: TextView = findViewById(R.id.tv_nombreUsuario)
            val iv_photoUser: ImageView = findViewById(R.id.iv_photoUser)

            tv_nombre.text = name
            // Insertamos la imagen en el image view
            Glide.with(this)
                .load(photoURI)
                .into(iv_photoUser)
        }
        val btnMisDatos = findViewById<TextView>(R.id.tv_misDatos)
        val btnMisPrestamos = findViewById<TextView>(R.id.tv_misPrestamos)
        val btnMisRentas = findViewById<TextView>(R.id.tv_misRentas)
        val btnMisTarjetas = findViewById<TextView>(R.id.tv_misTarjetas)
        val btnMiPrivacidad = findViewById<TextView>(R.id.tv_privacidad)
        val btnCerrarMiSesion = findViewById<TextView>(R.id.tv_cerrarSesion)

        btnMisDatos.setOnClickListener{
            var intent = Intent(this, DatosPersonalesActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("photo", photoURI)

            startActivity(intent)
        }

        btnMisPrestamos.setOnClickListener{
            var intent = Intent(this, MisPrestamosActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("name", name)
            intent.putExtra("email", correo)
            startActivity(intent)
        }

        btnMisRentas.setOnClickListener{
            var intent = Intent(this, MisRentasActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("name", name)
            intent.putExtra("email", correo)
            startActivity(intent)
        }

        btnMisTarjetas.setOnClickListener{
            var intent: Intent = Intent(this, MisTarjetasActivity::class.java)
            intent.putExtra("seleccion",1)
            intent.putExtra("id", id)
            startActivity(intent)
        }

        btnMiPrivacidad.setOnClickListener{
            var intent: Intent = Intent(this, PrivacidadActivity::class.java)
            startActivity(intent)
        }

        btnCerrarMiSesion.setOnClickListener{
            cerrarSesion()
        }
    }

    fun btnHome(view: View){
        var intent = Intent(this, PrincipalActivity::class.java)
        intent.putExtra("name",name)
        intent.putExtra("email",correo)
        intent.putExtra("id",id)
        intent.putExtra("photo",photoURI)
        startActivity(intent)
    }

    fun cerrarSesion() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Estás seguro de cerrar sesión?")
        builder.setPositiveButton("Sí") { _, _ ->
            // Si desea cerrar sesión
            val intent = Intent(this, InicioActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            signOut()
            finish()
        }
        builder.setNegativeButton("No") { _, _ ->
            // Si no desea cerrar sesión
        }
        val dialog = builder.create()
        dialog.show()
    }

    // Cierra la sesión gmail actual del usuario.
    fun signOut(){
        val googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)

        googleSignInClient.signOut().addOnCompleteListener(this) {
            Toast.makeText(this, "Sesión terminada", Toast.LENGTH_SHORT).show()
        }
    }
}