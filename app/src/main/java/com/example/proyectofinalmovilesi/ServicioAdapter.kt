package com.example.proyectofinalmovilesi

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServicioAdapter(
    private val servicios: List<Servicio>,
    private val onMenuOptionSelected: (Int, String) -> Unit
) : RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder>() {

    inner class ServicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnCreateContextMenuListener {
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)

        init {
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            val inflater = MenuInflater(itemView.context)
            inflater.inflate(R.menu.menu_contextual_servicio, menu)
            menu?.findItem(R.id.menu_editar)?.setOnMenuItemClickListener {
                onMenuOptionSelected(adapterPosition, "editar")
                true
            }
            menu?.findItem(R.id.menu_eliminar)?.setOnMenuItemClickListener {
                onMenuOptionSelected(adapterPosition, "eliminar")
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_servicio, parent, false)
        return ServicioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        val servicio = servicios[position]
        holder.tvDescripcion.text = "Descripción: ${servicio.descripcion}"
        holder.tvMonto.text = "Monto: \$${servicio.monto}"
        holder.tvFecha.text = "Fecha: ${servicio.fecha}"
        holder.tvEstado.text = "Estado: ${servicio.estado}"

        // Añadir un long click listener para habilitar el menú contextual
        holder.itemView.setOnLongClickListener {
            holder.itemView.showContextMenu()
            true
        }
    }

    override fun getItemCount(): Int {
        return servicios.size
    }
}
