package com.example.listadecomprasapp.listas

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.listadecomprasapp.listas.ListaDeCompras
import com.example.listadecomprasapp.databinding.ItemListaBinding

class ListasAdapter(
    private var listas: List<ListaDeCompras>,
    private val onItemClick: (ListaDeCompras) -> Unit,
    private val onItemLongClick: (ListaDeCompras) -> Unit
) : RecyclerView.Adapter<ListasAdapter.ListaViewHolder>() {

    inner class ListaViewHolder(private val binding: ItemListaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lista: ListaDeCompras) {
            binding.textViewNomeLista.text = lista.nome

            if (lista.imageUrl != null) {
                binding.imageViewLista.load(lista.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_menu_gallery)
                    error(R.drawable.ic_menu_close_clear_cancel)
                }
            } else {
                binding.imageViewLista.setImageResource(R.drawable.ic_menu_gallery)
            }

            itemView.setOnClickListener {
                onItemClick(lista)
            }
            itemView.setOnLongClickListener {
                onItemLongClick(lista)
                true
            }
        }
    }

    fun atualizarListas(novaLista: List<ListaDeCompras>) {
        listas = novaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaViewHolder {
        val binding = ItemListaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListaViewHolder, position: Int) {
        holder.bind(listas[position])
    }

    override fun getItemCount(): Int {
        return listas.size
    }
}