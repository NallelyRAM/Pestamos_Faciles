package mx.itson.edu.prestamosfaciles.Entidades

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class User(
    var id: String? = null,
    var nombre: String? = null,
    var apellido: String? = null,
    var correo: String? = null,
    var telefono: String? = "",
    var fechaNacimiento: Timestamp? = null,
    //var ubicacion: GeoPoint? = GeoPoint(0.0, 0.0),
    var ubicacion: String? = "",
    var tarjetas: ArrayList<Tarjeta> = arrayListOf()
) {
    fun addTarjeta(tarjeta: Tarjeta) {
        this.tarjetas.add(tarjeta)
    }

    fun toMap(): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        map["id"] = id
        map["nombre"] = nombre
        map["apellido"] = apellido
        map["correo"] = correo
        map["telefono"] = telefono
        map["fechaNacimiento"] = fechaNacimiento
        map["ubicacion"] = ubicacion
        map["tarjetas"] = tarjetas
        return map
    }

    override fun toString(): String {
        return "User(id=$id, nombre=$nombre, apellido=$apellido, " +
                "correo=$correo, telefono=$telefono, fechaNacimiento=$fechaNacimiento, ubicacion=$ubicacion, tarjetas=$tarjetas)"
    }
}



