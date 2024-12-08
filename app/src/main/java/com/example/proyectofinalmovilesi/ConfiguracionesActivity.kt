package com.example.proyectofinalmovilesi

import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ConfiguracionesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_configuraciones) // Mover esta línea antes de usar findViewById

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.configuraciones_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a los elementos
        val toggleNotificaciones = findViewById<ToggleButton>(R.id.toggleNotificaciones)
        val btnMenuEmergente = findViewById<Button>(R.id.btnMenuIdioma)

        // Configuración del ToggleButton
        toggleNotificaciones.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Notificaciones activadas", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notificaciones desactivadas", Toast.LENGTH_SHORT).show()
            }
        }

        // Configuración del menú emergente
        btnMenuEmergente.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            val inflater: MenuInflater = popupMenu.menuInflater
            inflater.inflate(R.menu.menu_emergente, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.idioma_espanol -> {
                        Toast.makeText(this, "Seleccionaste Opción 1", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.idioma_ingles -> {
                        Toast.makeText(this, "Seleccionaste Opción 2", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.idioma_frances -> {
                        Toast.makeText(this, "Seleccionaste Opción 3", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }
}
