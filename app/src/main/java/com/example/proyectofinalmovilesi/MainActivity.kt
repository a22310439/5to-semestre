package com.example.proyectofinalmovilesi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a los botones
        val btnAddService = findViewById<ImageButton>(R.id.btnAddService)
        val btnEditService = findViewById<ImageButton>(R.id.btnEditService)
        val btnDeleteService = findViewById<ImageButton>(R.id.btnDeleteService)
        val btnViewService = findViewById<ImageButton>(R.id.btnViewService)

        // Listeners para cada botón
        btnAddService.setOnClickListener {
            val intent = Intent(this, AddServiceActivity::class.java)
            startActivity(intent)
        }

        btnEditService.setOnClickListener {
            val intent = Intent(this, EditServiceActivity::class.java)
            startActivity(intent)
        }

        btnDeleteService.setOnClickListener {
            val intent = Intent(this, DeleteServiceActivity::class.java)
            startActivity(intent)
        }

        btnViewService.setOnClickListener {
            val intent = Intent(this, ViewServiceActivity::class.java)
            startActivity(intent)
        }
    }

    // Inflar el menú en el ActionBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Manejar las selecciones del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_configuraciones -> {
                // Navegar a la actividad de Configuraciones
                val intent = Intent(this, ConfiguracionesActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_acerca_de -> {
                // Mostrar un diálogo de "Acerca de"
                mostrarDialogoAcercaDe()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarDialogoAcercaDe() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Acerca de")
            .setMessage("Esta es una aplicación para gestionar servicios técnicos.\nVersión 1.0")
            .setPositiveButton("Aceptar", null)
            .create()
        dialog.show()
    }
}
