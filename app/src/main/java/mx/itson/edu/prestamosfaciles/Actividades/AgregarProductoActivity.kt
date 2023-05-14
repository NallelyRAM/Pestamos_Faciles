package mx.itson.edu.prestamosfaciles.Actividades

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
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
    var ubicacionSeleccionada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

        llenarSpinner(null)

        val btn_back: Button = findViewById(R.id.btn_back)
        val btn_agregarImagen: Button = findViewById(R.id.btn_agregar_imagen)
        val btn_agregarProducto: Button = findViewById(R.id.btn_agregar_producto)

        val sp_ubicacion = findViewById<Spinner>(R.id.sp_categoria_colonia)
        val dataSpinner: Array<String> = llenarSpinnerUbicacion()
        val adapterUbicacion = ArrayAdapter(this, android.R.layout.simple_spinner_item, dataSpinner)
        val editTextSearch: EditText = findViewById(R.id.editTextSearch)
        adapterUbicacion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sp_ubicacion.adapter = adapterUbicacion

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
                btn_agregarProducto.isEnabled = false
                uploadImageToStorage(fileUri)
                if(producto?.imagen != null){
                    guardarProductoStorage(producto)
                }
            }
        }


        // Configurar el TextWatcher para realizar la búsqueda en el Spinner
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val searchText = editable.toString().toLowerCase()

                // Filtrar los datos del Spinner en base al texto de búsqueda
                val filteredData = dataSpinner.filter { item ->
                    item.lowercase().contains(searchText)
                }

                // Actualizar el adaptador del Spinner con los datos filtrados
                val filteredAdapter = ArrayAdapter(this@AgregarProductoActivity, android.R.layout.simple_spinner_item, filteredData)
                filteredAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sp_ubicacion.adapter = filteredAdapter
            }
        })


        sp_ubicacion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                ubicacionSeleccionada = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }

    }
    private fun llenarCampos(producto: Producto){
        llenarSpinner(producto)

        val btn_agregarImagen: Button = findViewById(R.id.btn_agregar_imagen)
        val et_nombreProducto: EditText = findViewById(R.id.et_nombre_producto)
        val et_descripcionProducto: EditText = findViewById(R.id.et_descripcion)
        val et_precioProducto: EditText = findViewById(R.id.et_precio_alquiler)
        val sp_ubicacion = findViewById<Spinner>(R.id.sp_categoria_colonia)

        val adapterUbicacion = ArrayAdapter(this, android.R.layout.simple_spinner_item, llenarSpinnerUbicacion())
        adapterUbicacion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_ubicacion.adapter = adapterUbicacion
        val position = adapterUbicacion.getPosition(producto.ubicacion)
        sp_ubicacion.setSelection(position)
        ubicacionSeleccionada = producto.ubicacion

        btn_agregarImagen.text = producto.id
        et_nombreProducto.setText(producto.nombre)
        et_descripcionProducto.setText(producto.descripcion)
        et_precioProducto.setText(producto.precio.toString())

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

        if(ubicacionSeleccionada == "Seleccione una colonia" || ubicacionSeleccionada.isEmpty()){
            Toast.makeText(this, "Favor de asignarle una colonia válida", Toast.LENGTH_LONG).show()
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

        val nombre = et_nombreProducto.text.toString()
        val descripcion = et_descripcionProducto.text.toString()
        val precio = et_precioProducto.text.toString().toDouble()

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
                    ubicacion = ubicacionSeleccionada,
                    idVendedor = idVendedor
                )
                productoRef.add(producto).addOnCompleteListener {
                    Toast.makeText(this, "Se agregó el producto correctamente a nuestro catalogo", Toast.LENGTH_LONG).show()
                    finish()
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
                    ubicacion = ubicacionSeleccionada,
                    idVendedor = producto!!.idVendedor
                )
                productoRef.document(productoId).set(producto).addOnCompleteListener {
                    Toast.makeText(this, "Se actualizó el producto correctamente en nuestro catalogo", Toast.LENGTH_LONG).show()
                    finish()
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

    private fun llenarSpinnerUbicacion(): Array<String> {
        val categoriasColonias = arrayOf("Seleccione una colonia",
            "Agronomos",
            "Alameda del Cedro",
            "Alameda del Cedro II",
            "Algodones",
            "Alta California",
            "Altar Residencial",
            "Amanecer 1",
            "Amanecer 2",
            "Ampliación Miguel Alemán",
            "Ampliación Miravalle",
            "Aves Del Castillo",
            "Bellavista",
            "Benito Juárez",
            "Bosque Del Nainari",
            "Bugambilias",
            "Cajeme",
            "Campanario",
            "Campestre",
            "Campestre 2da. Ampliación",
            "Casa Blanca",
            "Casa Blanca Ampliación II",
            "Casa Real",
            "Cd. Obregón (Ciudad Obregón)",
            "Central de Abastos",
            "Chapultepec",
            "Chihuahua",
            "Cincuentenario",
            "Ciudad Obregón Centro (Fundo Legal)",
            "Colinas del Yaqui",
            "Constitución",
            "Cortinas 1ra. Sección",
            "Cortinas 2da. Sección",
            "Cortinas 3ra. Sección",
            "Cortinas 4ta. Sección",
            "Cuauhtémoc Cárdenas",
            "Cuauhtémoc (Urbanizable 6)",
            "Cumuripa",
            "Del Bosque",
            "Del Lago",
            "Del Valle",
            "Ejidatarios",
            "Ejido Cajeme",
            "Electricista",
            "El Paraíso",
            "El Roble",
            "El Rodeo",
            "Esperanza Tiznada",
            "Faustino Félix Serna",
            "Fovissste",
            "Fovissste 2",
            "Fovissste 3",
            "Francisco Eusebio Kino",
            "Franja Comercial 300",
            "Fuentes Del Bosque",
            "Galeana",
            "Girasoles",
            "Granjas FOVISSSTE Norte (Codornices)",
            "Hacienda Del Sol",
            "Hacienda Real",
            "Haciendas El Rosario",
            "Haciendas San Francisco",
            "Haciendas San Miguel",
            "Herradura",
            "Hidalgo",
            "Infonavit ",
            "ISSSTESON",
            "Jardines Del Valle",
            "Ladrillera",
            "La Florida",
            "La Joya Villa California IV",
            "La Misión",
            "La Reforma",
            "Las Arboledas",
            "Las Brisas",
            "Las Campanas",
            "Las Espigas",
            "Las Flores",
            "Las Fuentes",
            "Las Fuentes II",
            "Las Haciendas",
            "Las Misiones",
            "Las Palmas",
            "Las Puertas",
            "Las Torres",
            "Lázaro Mercado",
            "Libertad",
            "Linda Vista",
            "Los Álamos 1",
            "Los Álamos 2",
            "Los Alisos",
            "Los Angeles",
            "Los Arcos",
            "Los Encinos",
            "Los Encinos II",
            "Los Olivos",
            "Los Patios",
            "Los Portales",
            "Los Presidentes",
            "Los Sauces",
            "Luis Donaldo Colosio",
            "Luis Echeverría Álvarez (Álvaro Obregón)",
            "Manlio Fabio Beltrones",
            "Matías Mendez",
            "Maximiliano Rubio López",
            "México",
            "Mirasierra",
            "Mirasoles",
            "Miravalle",
            "Misión del Real",
            "Misión Del Sol",
            "Misioneros",
            "Misión San Javier",
            "Misión San Rafael",
            "Montecarlo",
            "Morelos",
            "Multifamiliares IMSS",
            "Municipio Libre",
            "Nainari Del Yaqui",
            "Noroeste",
            "Nueva Galicia",
            "Nueva Palmira",
            "Nuevo Amanecer",
            "Nuevo Cajeme",
            "Nuevo Real del Norte",
            "Ostimuri",
            "Otancahui",
            "Palmar",
            "Palma Real",
            "París",
            "Parque Industrial",
            "Parque Tecnológico",
            "Paseo Alameda",
            "Pedregal",
            "Pioneros",
            "Pioneros de Cajeme",
            "Posada del Sol",
            "Pradera Bonita",
            "Prados de La Laguna",
            "Prados Del Tepeyac",
            "Primavera",
            "Primero de Mayo",
            "Privada de La Laguna",
            "Puente Real",
            "Quinta Diaz",
            "Quinta Real",
            "Real Campestre",
            "Real Del Arco",
            "Real Del Bosque",
            "Real Del Sol",
            "Real del Sol Ampliación",
            "Real Del Valle",
            "Real de Sabinos",
            "Real de Sevilla",
            "Rincón Del Valle",
            "Robles Del Castillo",
            "Russo Vogel",
            "San Anselmo",
            "San Antonio",
            "San Juan Capistrano",
            "Santa Anita",
            "Santa Fe",
            "Sierra Vista",
            "Sochiloa",
            "Sonora",
            "Sóstenes Valenzuela",
            "Torres de París",
            "Ultratec Aves",
            "Urbanizable 2",
            "Urbanizable 3",
            "Urbanizable 4",
            "Urbanizable 5",
            "Urbanizable 6 (Cuauhtémoc) Ampliación",
            "Urbanizable 7",
            "Urbanizable I",
            "Valle del Sol",
            "Valle Dorado",
            "Valle Verde",
            "Villa Alegre",
            "Villa Aurora",
            "Villa California",
            "Villa California 2",
            "Villa del Nainari",
            "Villa del Rey Sección Colonial",
            "Villa Del Tetabiate",
            "Villa Florencia",
            "Villa Fontana",
            "Villa Guadalupe",
            "Villa Itson",
            "Villa Mezquite",
            "Villa Satélite",
            "Villas de Cortés",
            "Villas del Campestre",
            "Villas del Palmar",
            "Villas del Real",
            "Villas Del Rey",
            "Villas Del Sol",
            "Villas de Trigo",
            "Vista Hermosa",
            "Zona Norte",
            "Zona Norte Comercial",
        )

        return categoriasColonias;
    }

}