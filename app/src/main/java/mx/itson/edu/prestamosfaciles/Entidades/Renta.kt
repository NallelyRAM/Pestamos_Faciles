package mx.itson.edu.prestamosfaciles.Entidades

import com.google.firebase.Timestamp
import java.util.*

fun sumarDias(timestamp: Timestamp, dias: Int): Timestamp {
    val calendar = Calendar.getInstance()
    calendar.time = timestamp.toDate()
    calendar.add(Calendar.DAY_OF_YEAR, dias)
    return Timestamp(calendar.time)
}
class Renta(
    var id: String = UUID.randomUUID().toString(),
    var usuario: User = User(),
    var producto: Producto = Producto(),
    var tarjeta: Tarjeta = Tarjeta(),
    var totalRenta: Double = 0.0,
    var fechaRenta: Timestamp = Timestamp.now(),
    var fechaVencimiento: Timestamp = sumarDias(fechaRenta, 7)
) : java.io.Serializable{
    init {
        fechaVencimiento = sumarDias(fechaRenta, 7)
    }
}
