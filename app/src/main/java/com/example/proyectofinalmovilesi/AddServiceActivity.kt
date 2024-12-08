package com.example.proyectofinalmovilesi

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*
import java.util.*

class AddServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_service)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_service)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a los campos
        val etDescripcion = findViewById<EditText>(R.id.etDescripcion)
        val etMonto = findViewById<EditText>(R.id.etMonto)
        val etFecha = findViewById<EditText>(R.id.etFecha)
        val etHora = findViewById<EditText>(R.id.etHora)
        val spinnerCliente = findViewById<Spinner>(R.id.spinnerCliente)
        val etComentarios = findViewById<EditText>(R.id.etComentarios)
        val rgEstado = findViewById<RadioGroup>(R.id.rgEstado)
        val cbPagado = findViewById<CheckBox>(R.id.cbPagado)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        // Configurar Spinner
        val clientes = listOf("Cliente 1", "Cliente 2", "Cliente 3")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, clientes)
        spinnerCliente.adapter = adapter

        // Configurar DatePickerDialog
        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    etFecha.setText("$dayOfMonth/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Configurar TimePickerDialog
        etHora.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    etHora.setText(String.format("%02d:%02d", hour, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        // Botón Guardar
        btnGuardar.setOnClickListener {
            val descripcion = etDescripcion.text.toString()
            val monto = etMonto.text.toString().toDoubleOrNull() ?: 0.0
            val fecha = etFecha.text.toString()
            val hora = etHora.text.toString()
            val cliente = spinnerCliente.selectedItem.toString()
            val comentarios = etComentarios.text.toString()
            val estado = if (rgEstado.checkedRadioButtonId == R.id.rbPendiente) "Pendiente" else "Terminado"
            val pagado = cbPagado.isChecked

            // Crear un nuevo servicio
            val servicio = Servicio(descripcion, monto, fecha, hora, cliente, comentarios, estado, pagado)

            // Agregar al ServicioManager
            ServicioManager.agregarServicio(servicio)

            Toast.makeText(this, "Servicio guardado correctamente", Toast.LENGTH_SHORT).show()
            finish() // Finalizar la actividad
        }

    }
}