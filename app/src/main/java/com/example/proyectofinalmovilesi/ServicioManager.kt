package com.example.proyectofinalmovilesi

object ServicioManager {
    private val servicios = mutableListOf<Servicio>()

    // Obtener todos los servicios
    fun getServicios(): List<Servicio> {
        return servicios
    }

    // Agregar un nuevo servicio
    fun agregarServicio(servicio: Servicio) {
        servicios.add(servicio)
    }

    // Eliminar un servicio por posición
    fun eliminarServicio(posicion: Int) {
        if (posicion in servicios.indices) {
            servicios.removeAt(posicion)
        }
    }

    // Editar un servicio
    fun editarServicio(posicion: Int, nuevoServicio: Servicio) {
        if (posicion in servicios.indices) {
            servicios[posicion] = nuevoServicio
        }
    }
}
