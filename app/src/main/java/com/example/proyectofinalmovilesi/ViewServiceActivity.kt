package com.example.proyectofinalmovilesi

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinalmovilesi.fragments.ServiceListFragment

class ViewServiceActivity : AppCompatActivity() {

    private lateinit var serviceListFragment: ServiceListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_service)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_service)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cargar el fragmento
        serviceListFragment = ServiceListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, serviceListFragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        // Actualizar la lista del fragmento al regresar
        serviceListFragment.actualizarLista()
    }
}