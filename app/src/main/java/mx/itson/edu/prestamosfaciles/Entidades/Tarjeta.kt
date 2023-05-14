package mx.itson.edu.prestamosfaciles.Entidades

import java.util.*
class Tarjeta(
    var numTarjeta: String,
    var mesCaducidad: String,
    var anioCaducidad: String,
    var codigoPostal: String,
    var nombreTitular: String,
    var apellidoTitular: String,
    var CVV: String,
    var emisor: String
) : java.io.Serializable {

    constructor() : this("", "", "", "", "", "", "", "")

    override fun toString(): String {
        return "Tarjeta(numTarjeta='$numTarjeta', mesCaducidad='$mesCaducidad', anioCaducidad='$anioCaducidad', codigoPostal='$codigoPostal', nombreTitular='$nombreTitular', apellidoTitular='$apellidoTitular', CVV='$CVV', emisor='$emisor')"
    }
    fun toMap(): Map<String, Any> {
        val map = HashMap<String, Any>()
        map["numTarjeta"] = numTarjeta
        map["mesCaducidad"] = mesCaducidad
        map["anioCaducidad"] = anioCaducidad
        map["codigoPostal"] = codigoPostal
        map["nombreTitular"] = nombreTitular
        map["apellidoTitular"] = apellidoTitular
        map["CVV"] = CVV
        map["emisor"] = emisor
        return map
    }
}
