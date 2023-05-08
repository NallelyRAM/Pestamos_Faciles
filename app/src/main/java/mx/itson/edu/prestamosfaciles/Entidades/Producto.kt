package mx.itson.edu.prestamosfaciles.Entidades

import android.net.Uri


class Producto (var id: String,
                var nombre: String,
                var descripcion: String,
                var imagen: Uri?,
                var categoria: String,
                var precio:Double,
                var ubicacion: String)