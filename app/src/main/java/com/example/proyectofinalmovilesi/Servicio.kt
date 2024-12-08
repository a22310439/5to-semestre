package com.example.proyectofinalmovilesi

data class Servicio(
    val descripcion: String,
    val monto: Double,
    val fecha: String,
    val hora: String,
    val cliente: String,
    val comentarios: String,
    val estado: String,
    val pagado: Boolean
)
