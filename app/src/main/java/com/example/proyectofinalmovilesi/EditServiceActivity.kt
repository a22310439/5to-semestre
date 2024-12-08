package com.example.proyectofinalmovilesi

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*

class EditServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_service)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit_service)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a los elementos
        val spinnerServicio = findViewById<Spinner>(R.id.spinnerServicio)
        val etDescripcion = findViewById<EditText>(R.id.etDescripcion)
        val etMonto = findViewById<EditText>(R.id.etMonto)
        val etFecha = findViewById<EditText>(R.id.etFecha)
        val etHora = findViewById<EditText>(R.id.etHora)
        val spinnerCliente = findViewById<Spinner>(R.id.spinnerCliente)
        val etComentarios = findViewById<EditText>(R.id.etComentarios)
        val rgEstado = findViewById<RadioGroup>(R.id.rgEstado)
        val cbPagado = findViewById<CheckBox>(R.id.cbPagado)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val formulario = findViewById<LinearLayout>(R.id.formulario)

        // Obtener servicios del ServicioManager
        val servicios = ServicioManager.getServicios()
        val opcionesSpinner = listOf("Seleccionar servicio") + servicios.map { it.descripcion }

        // Configurar el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesSpinner)
        spinnerServicio.adapter = adapter

        // Leer el índice del servicio seleccionado desde el Intent (si existe)
        val servicioIndex = intent.getIntExtra("SERVICIO_INDEX", -1)

        if (servicioIndex != -1 && servicioIndex < servicios.size) {
            spinnerServicio.setSelection(servicioIndex + 1) // +1 por la opción "Seleccionar servicio"
            val servicio = servicios[servicioIndex]

            formulario.visibility = View.VISIBLE
            etDescripcion.setText(servicio.descripcion)
            etMonto.setText(servicio.monto.toString())
            etFecha.setText(servicio.fecha)
            etHora.setText(servicio.hora)

            // Configurar el Spinner de clientes
            val clientes = listOf("Cliente 1", "Cliente 2", "Cliente 3")
            val clienteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, clientes)
            spinnerCliente.adapter = clienteAdapter
            spinnerCliente.setSelection(clientes.indexOf(servicio.cliente))

            etComentarios.setText(servicio.comentarios)
            rgEstado.check(if (servicio.estado == "Pendiente") R.id.rbPendiente else R.id.rbTerminado)
            cbPagado.isChecked = servicio.pagado
        } else {
            // Si no se pasa un índice válido, comenzar con el formulario oculto
            formulario.visibility = View.GONE
            spinnerServicio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position == 0) {
                        formulario.visibility = View.GONE
                    } else {
                        formulario.visibility = View.VISIBLE
                        val servicio = servicios[position - 1]
                        etDescripcion.setText(servicio.descripcion)
                        etMonto.setText(servicio.monto.toString())
                        etFecha.setText(servicio.fecha)
                        etHora.setText(servicio.hora)

                        // Configurar los datos en el formulario
                        val clientes = listOf("Cliente 1", "Cliente 2", "Cliente 3")
                        val clienteAdapter = ArrayAdapter(this@EditServiceActivity, android.R.layout.simple_spinner_item, clientes)
                        spinnerCliente.adapter = clienteAdapter
                        spinnerCliente.setSelection(clientes.indexOf(servicio.cliente))

                        etComentarios.setText(servicio.comentarios)
                        rgEstado.check(if (servicio.estado == "Pendiente") R.id.rbPendiente else R.id.rbTerminado)
                        cbPagado.isChecked = servicio.pagado
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    formulario.visibility = View.GONE
                }
            }
        }

        // Acción para el botón Guardar
        btnGuardar.setOnClickListener {
            val selectedPosition = spinnerServicio.selectedItemPosition
            if (selectedPosition <= 0 || selectedPosition > servicios.size) {
                Toast.makeText(this, "Por favor, selecciona un servicio válido", Toast.LENGTH_SHORT).show()
            } else {
                val descripcion = etDescripcion.text.toString()
                val monto = etMonto.text.toString().toDoubleOrNull() ?: 0.0
                val fecha = etFecha.text.toString()
                val hora = etHora.text.toString()
                val cliente = spinnerCliente.selectedItem.toString()
                val comentarios = etComentarios.text.toString()
                val estado = if (rgEstado.checkedRadioButtonId == R.id.rbPendiente) "Pendiente" else "Terminado"
                val pagado = cbPagado.isChecked

                // Crear el nuevo servicio
                val nuevoServicio = Servicio(descripcion, monto, fecha, hora, cliente, comentarios, estado, pagado)

                // Editar o agregar el servicio
                ServicioManager.editarServicio(selectedPosition - 1, nuevoServicio)

                Toast.makeText(this, "Servicio editado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}