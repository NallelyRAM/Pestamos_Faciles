package mx.itson.edu.prestamosfaciles.Entidades

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class User(
    var id: String ?= null,
    var nombre: String ?= null,
    var apellido: String ?= null,
    var correo: String ?= null,
    var telefono: String ?= "",
    var fechaNacimiento: Timestamp?= null,
    var ubicacion: GeoPoint?= GeoPoint(0.0, 0.0)) {

    override fun toString(): String {
        return "User(id=$id, nombre=$nombre, apellido=$apellido, correo=$correo, telefono=$telefono, fechaNacimiento=$fechaNacimiento, ubicacion=$ubicacion)"
    }
}


