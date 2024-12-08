package com.example.proyectofinalmovilesi

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*
import androidx.appcompat.app.AlertDialog

class DeleteServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_delete_service)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.delete_service)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a los elementos
        val spinnerServicio = findViewById<Spinner>(R.id.spinnerServicio)
        val btnEliminar = findViewById<Button>(R.id.btnEliminar)
        val detallesServicio = findViewById<LinearLayout>(R.id.detallesServicio)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescripcion)
        val tvMonto = findViewById<TextView>(R.id.tvMonto)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        val tvHora = findViewById<TextView>(R.id.tvHora)
        val tvCliente = findViewById<TextView>(R.id.tvCliente)
        val tvComentarios = findViewById<TextView>(R.id.tvComentarios)
        val tvEstado = findViewById<TextView>(R.id.tvEstado)
        val tvPagado = findViewById<TextView>(R.id.tvPagado)

        // Obtener servicios del ServicioManager
        val servicios = ServicioManager.getServicios()
        val opcionesSpinner = listOf("Seleccionar servicio") + servicios.map { it.descripcion }

        // Configurar el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesSpinner)
        spinnerServicio.adapter = adapter

        if (servicios.isEmpty()) {
            Toast.makeText(this, "No hay servicios disponibles para eliminar", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Manejar selección en el Spinner
        spinnerServicio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    // Ocultar detalles si no hay selección
                    detallesServicio.visibility = View.GONE
                } else {
                    // Mostrar detalles del servicio seleccionado
                    val servicio = servicios[position - 1]
                    detallesServicio.visibility = View.VISIBLE
                    tvDescripcion.text = "Descripción: ${servicio.descripcion}"
                    tvMonto.text = "Monto: \$${servicio.monto}"
                    tvFecha.text = "Fecha: ${servicio.fecha}"
                    tvHora.text = "Hora: ${servicio.hora}"
                    tvCliente.text = "Cliente: ${servicio.cliente}"
                    tvComentarios.text = "Comentarios: ${servicio.comentarios}"
                    tvEstado.text = "Estado: ${servicio.estado}"
                    tvPagado.text = "Pagado: ${if (servicio.pagado) "Sí" else "No"}"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                detallesServicio.visibility = View.GONE
            }
        }

        // Acción para el botón Eliminar
        btnEliminar.setOnClickListener {
            val selectedPosition = spinnerServicio.selectedItemPosition
            if (selectedPosition <= 0 || selectedPosition > servicios.size) {
                Toast.makeText(this, "Por favor, selecciona un servicio válido", Toast.LENGTH_SHORT).show()
            } else {
                val servicioSeleccionado = servicios[selectedPosition - 1]

                // Mostrar AlertDialog de confirmación
                AlertDialog.Builder(this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro de que deseas eliminar el servicio '${servicioSeleccionado.descripcion}'?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        // Eliminar el servicio del ServicioManager
                        ServicioManager.eliminarServicio(selectedPosition - 1)

                        Toast.makeText(this, "Servicio eliminado: ${servicioSeleccionado.descripcion}", Toast.LENGTH_SHORT).show()

                        // Actualizar el Spinner después de eliminar
                        val updatedServicios = ServicioManager.getServicios()
                        if (updatedServicios.isEmpty()) {
                            Toast.makeText(this, "No quedan servicios disponibles", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            val updatedAdapter = ArrayAdapter(
                                this,
                                android.R.layout.simple_spinner_item,
                                listOf("Seleccionar servicio") + updatedServicios.map { it.descripcion }
                            )
                            spinnerServicio.adapter = updatedAdapter
                            detallesServicio.visibility = View.GONE
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }
}