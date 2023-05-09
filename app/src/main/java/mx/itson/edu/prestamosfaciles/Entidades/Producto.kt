package mx.itson.edu.prestamosfaciles.Entidades

import java.io.Serializable

class Producto : Serializable {
    var id: String = ""
    var nombre: String = ""
    var descripcion: String = ""
    var imagen: String? = null
    var categoria: String = ""
    var precio: Double = 0.0
    var ubicacion: String = ""
    var idVendedor: String = ""

    constructor()

    constructor(id: String, nombre: String, descripcion: String, imagen: String?, categoria: String, precio: Double, ubicacion: String, idVendedor: String) {
        this.id = id
        this.nombre = nombre
        this.descripcion = descripcion
        this.imagen = imagen
        this.categoria = categoria
        this.precio = precio
        this.ubicacion = ubicacion
        this.idVendedor = idVendedor
    }
}
