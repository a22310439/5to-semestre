package com.example.proyectofinalmovilesi.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalmovilesi.EditServiceActivity
import com.example.proyectofinalmovilesi.R
import com.example.proyectofinalmovilesi.Servicio
import com.example.proyectofinalmovilesi.ServicioAdapter
import com.example.proyectofinalmovilesi.ServicioManager

class ServiceListFragment : Fragment(R.layout.fragment_service_list) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ServicioAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewServicios)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val servicios = ServicioManager.getServicios()

        adapter = ServicioAdapter(servicios) { position, action ->
            when (action) {
                "editar" -> {
                    // Navegar a EditServiceActivity con el índice del servicio
                    val intent = Intent(requireContext(), EditServiceActivity::class.java)
                    intent.putExtra("SERVICIO_INDEX", position)
                    startActivity(intent)
                }
                "eliminar" -> {
                    // Mostrar diálogo de confirmación
                    mostrarDialogoEliminacion(position)
                }
            }
        }

        recyclerView.adapter = adapter
    }

    private fun mostrarDialogoEliminacion(position: Int) {
        val servicio = ServicioManager.getServicios()[position]

        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar el servicio '${servicio.descripcion}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                // Eliminar servicio del ServicioManager
                ServicioManager.eliminarServicio(position)
                adapter.notifyItemRemoved(position)
                Toast.makeText(context, "Servicio eliminado: ${servicio.descripcion}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()


    }

    fun actualizarLista() {
        adapter.notifyDataSetChanged()
    }
}