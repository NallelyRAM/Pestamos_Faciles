package mx.itson.edu.prestamosfaciles.Actividades

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import mx.itson.edu.prestamosfaciles.Entidades.Producto
import mx.itson.edu.prestamosfaciles.R
import java.util.*


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

    var producto: Producto? = null
    var idUsuario : String = ""
    var seleccion: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

        llenarSpinner(null)

        val btn_back: Button = findViewById(R.id.btn_back)
        val btn_agregarImagen: Button = findViewById(R.id.btn_agregar_imagen)
        val btn_agregarProducto: Button = findViewById(R.id.btn_agregar_producto)


        val bundle = intent.extras
        if(bundle != null){
            try{
                producto = intent.getSerializableExtra("producto") as Producto
            }catch(e: Exception){

            }
            idUsuario = bundle.getString("idUsuario").toString()
            seleccion = bundle.getString("actualizar")

            producto?.let { llenarCampos(it) }

        }


        btn_agregarImagen.setOnClickListener {
            fileUpload()
        }

        btn_back.setOnClickListener {
            finish()
        }

        btn_agregarProducto.setOnClickListener{
            if(validaciones()){
                finish()
                uploadImageToStorage(fileUri)
                if(producto?.imagen != null){
                    guardarProductoStorage(producto)
                }
            }
        }



    }

    private fun llenarCampos(producto: Producto){
        llenarSpinner(producto)

        val btn_agregarImagen: Button = findViewById(R.id.btn_agregar_imagen)
        val et_nombreProducto: EditText = findViewById(R.id.et_nombre_producto)
        val et_descripcionProducto: EditText = findViewById(R.id.et_descripcion)
        val et_precioProducto: EditText = findViewById(R.id.et_precio_alquiler)
        val et_ubicacionProducto: EditText = findViewById(R.id.et_nombre_ubicacion)

        btn_agregarImagen.text = producto.id
        et_nombreProducto.setText(producto.nombre)
        et_descripcionProducto.setText(producto.descripcion)
        et_precioProducto.setText(producto.precio.toString())
        et_ubicacionProducto.setText(producto.ubicacion)

    }

    /**
     * Aqui van a hacer todas las validaciones, incluyendo la de imagen,
     * preguntando que fileUri no sea nulo y fileName no sea vacio.
     *
     * Para cualquier otra duda, mensajito a discord a Brayan:)
     */
    private fun validaciones() : Boolean{
        val et_nombreProducto: EditText = findViewById(R.id.et_nombre_producto)
        val et_descripcionProducto: EditText = findViewById(R.id.et_descripcion)
        val et_precioProducto: EditText = findViewById(R.id.et_precio_alquiler)
        val et_ubicacionProducto: EditText = findViewById(R.id.et_nombre_ubicacion)

        if(categoriaSeleccionada=="Seleccione una categoría"){
            Toast.makeText(this, "Selecciona una categoria", Toast.LENGTH_LONG).show()
            return false
        }
        if(fileName==""&& producto?.imagen ==null){

            Toast.makeText(this, "Favor de seleccionar una imagen del producto", Toast.LENGTH_LONG).show()
            return false
        }
        if(et_nombreProducto.text.toString()==""){
            Toast.makeText(this, "Favor de asignarle un nombre al producto", Toast.LENGTH_LONG).show()
            return false
        }
        if(et_descripcionProducto.text.toString()==""){
            Toast.makeText(this, "Favor de asignarle una descripción al producto", Toast.LENGTH_LONG).show()
            return false
        }
        if(et_precioProducto.text.toString()==""){
            Toast.makeText(this, "Favor de asignarle un precio al producto", Toast.LENGTH_LONG).show()
            return false
        }
        if(et_ubicacionProducto.text.toString()==""){
            Toast.makeText(this, "Favor de asignarle una ubicación", Toast.LENGTH_LONG).show()
            return false
        }
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
                val btn_agregarImagen: Button = findViewById(R.id.btn_agregar_imagen)
                btn_agregarImagen.text = fileName
                fileName = UUID.randomUUID().toString()

            }
        }
    }
    private fun uploadImageToStorage(fileUri: Uri?){
        val folderRef: StorageReference = FirebaseStorage.getInstance().reference.child("prestodo_objetos")


        if(seleccion.equals("actualizar")){
            val fileRef: StorageReference = folderRef.child(producto!!.id)
            // Actualizar el archivo
            if (fileUri != null) {
                fileRef.putFile(fileUri).addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        filePathDownload = uri.toString() // <-- se obtiene la URL aquí
                        val hashMap = HashMap<String, String>()
                        hashMap["link"] = uri.toString()
                        myRef.setValue(hashMap)
                        guardarProductoStorage(producto)
                        Log.d("Mensaje", "Se subió correctamente")
                    }
                }.addOnFailureListener {
                    // Se produjo un error al actualizar el archivo
                }
            }
        } else {
            if (fileUri != null) {
                val fileRef: StorageReference = folderRef.child(fileName)
                fileRef.putFile(fileUri).addOnSuccessListener { taskSnapshot ->
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        filePathDownload = uri.toString() // <-- se obtiene la URL aquí
                        val hashMap = HashMap<String, String>()
                        hashMap["link"] = uri.toString()
                        myRef.setValue(hashMap)
                        guardarProductoStorage(producto)
                        Log.d("Mensaje", "Se subió correctamente")
                    }
                }
            }
        }
    }

    private fun guardarProductoStorage(producto: Producto?) {

        val et_nombreProducto: EditText = findViewById(R.id.et_nombre_producto)
        val et_descripcionProducto: EditText = findViewById(R.id.et_descripcion)
        val et_precioProducto: EditText = findViewById(R.id.et_precio_alquiler)
        val et_ubicacionProducto: EditText = findViewById(R.id.et_nombre_ubicacion)


        val nombre = et_nombreProducto.text.toString()
        val descripcion = et_descripcionProducto.text.toString()
        val precio = et_precioProducto.text.toString().toDouble()
        val ubicacion = et_ubicacionProducto.text.toString()

        val id: String = if(producto == null){
            val fullFileName = fileName
            fullFileName
        } else {
            producto.id
        }

        val bundle = intent?.extras

        var idVendedor = bundle?.getString("id").toString()

        // Query to search for products with the same name
        val query = productoRef.whereEqualTo("id", producto?.id)

        // Check if a product with the same name already exists
        query.get().addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                // Product does not exist, add it to the database
                val producto = Producto(
                    id = id,
                    nombre = nombre,
                    descripcion = descripcion,
                    imagen = filePathDownload,
                    categoria = categoriaSeleccionada,
                    precio = precio,
                    ubicacion = ubicacion,
                    idVendedor = idVendedor
                )
                productoRef.add(producto).addOnCompleteListener {
                    Toast.makeText(this, "Se agregó el producto correctamente a nuestro catalogo", Toast.LENGTH_LONG).show()
                }
            } else {
                // Product already exists, update it
                val productoId = documents.first().id
                if(filePathDownload == null || filePathDownload!!.isEmpty()){
                    filePathDownload = producto!!.imagen
                }

                val producto = Producto(
                    id = id,
                    nombre = nombre,
                    descripcion = descripcion,
                    imagen = filePathDownload,
                    categoria = categoriaSeleccionada,
                    precio = precio,
                    ubicacion = ubicacion,
                    idVendedor = producto!!.idVendedor
                )
                productoRef.document(productoId).set(producto).addOnCompleteListener {
                    Toast.makeText(this, "Se actualizó el producto correctamente en nuestro catalogo", Toast.LENGTH_LONG).show()
                }
            }
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


    private fun llenarSpinner(producto: Producto?){
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

        if(producto != null){
            val position = adapter.getPosition(producto.categoria)
            spinner.setSelection(position)
        }

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