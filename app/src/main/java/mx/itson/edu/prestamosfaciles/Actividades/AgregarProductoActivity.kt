package mx.itson.edu.prestamosfaciles.Actividades

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import mx.itson.edu.prestamosfaciles.Entidades.Producto
import mx.itson.edu.prestamosfaciles.R
import java.io.File
import java.util.*
import kotlin.collections.HashMap


class AgregarProductoActivity : AppCompatActivity() {

    private val productoRef = FirebaseFirestore.getInstance().collection("productos")
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val File = 1
    private val database = Firebase.database
    val myRef = database.getReference("prestodo_objetos")
    var fileName = ""
    var fileUri: Uri? = null
    var categoriaSeleccionada: String = ""
    var filePathDownload: String? = ""

    object Constants {
        const val ID_REGEX = "PRDimg"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

        llenarSpinner()

        val btn_back: Button = findViewById(R.id.btn_back)
        val btn_agregarImagen: Button = findViewById(R.id.btn_agregar_imagen)
        val btn_agregarProducto: Button = findViewById(R.id.btn_agregar_producto)

        btn_agregarImagen.setOnClickListener {
            fileUpload()
        }

        btn_back.setOnClickListener {
            finish()
        }

        btn_agregarProducto.setOnClickListener{
            if(validaciones()){
                finish()
                fileUri?.let { it1 -> uploadImageToStorage(it1,fileName) }
            }
        }

    }

    /**
     * Aqui van a hacer todas las validaciones, incluyendo la de imagen,
     * preguntando que fileUri no sea nulo y fileName no sea vacio.
     *
     * Para cualquier otra duda, mensajito a discord a Brayan:)
     */
    private fun validaciones() : Boolean{

        return true
    }

    fun fileUpload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, File)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == File) {
            if (resultCode == RESULT_OK) {
                fileUri = data!!.data
                fileName = getFileName(fileUri)

                val productoID = Constants.ID_REGEX +" "+ UUID.randomUUID().toString()

                val newFileName = File(fileName).nameWithoutExtension +" "+productoID
                val btn_agregarImagen: Button = findViewById(R.id.btn_agregar_imagen)
                btn_agregarImagen.text = fileName
                val extension = fileName.substring(fileName.lastIndexOf("."))
                fileName = newFileName+extension

            }
        }
    }
    private fun uploadImageToStorage(fileUri: Uri, fileName: String){
        val folderRef: StorageReference = FirebaseStorage.getInstance().reference.child("prestodo_objetos")
        val fileRef: StorageReference = folderRef.child(fileName)
        if (fileUri != null) {
            fileRef.putFile(fileUri).addOnSuccessListener { taskSnapshot ->
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    filePathDownload = uri.toString() // <-- se obtiene la URL aquí
                    val hashMap = HashMap<String, String>()
                    hashMap["link"] = uri.toString()
                    myRef.setValue(hashMap)
                    Log.d("Mensaje", "Se subió correctamente")
                    guardarProductoStorage()
                }
            }
        }
    }

    private fun guardarProductoStorage(){
        val et_nombreProducto: EditText = findViewById(R.id.et_nombre_producto)
        val et_descripcionProducto: EditText = findViewById(R.id.et_descripcion)
        val et_precioProducto: EditText = findViewById(R.id.et_precio_alquiler)
        val et_ubicacionProducto: EditText = findViewById(R.id.et_nombre_ubicacion)


        val fullFileName = fileName.split(" ")
        val nombre = et_nombreProducto.text.toString()
        val descripcion = et_descripcionProducto.text.toString()
        val precio = et_precioProducto.text.toString().toDouble()
        val ubicacion = et_ubicacionProducto.text.toString()
        val id = fullFileName[2]

        val bundle = intent?.extras

        val producto = Producto(id = id,
            nombre = nombre,
            descripcion = descripcion,
            imagen = filePathDownload,
            categoria = categoriaSeleccionada,
            precio = precio,
            ubicacion = ubicacion,
            idVendedor = bundle?.getString("id").toString()
        )
        productoRef.add(producto).addOnCompleteListener{
            Toast.makeText(this, "Se agregó el producto correctamente a nuestro catalogo", Toast.LENGTH_LONG).show()

        }
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri?): String {
        var result: String? = null
        if (uri?.scheme.equals("content")) {
            val cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri?.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "unknown"
    }


    private fun llenarSpinner(){
        val categoriasProductos = arrayOf("Seleccione una categoría",
                                            "Electrónica",
                                            "Moda", "Hogar y jardín",
                                            "Belleza y cuidado personal",
                                            "Deportes y actividades al aire libre",
                                            "Juguetes y juegos", "Alimentos y bebidas",
                                            "Libros y medios")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriasProductos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner: Spinner = findViewById(R.id.sp_categoria_producto)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Categoría seleccionado
                categoriaSeleccionada = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }
    }



}