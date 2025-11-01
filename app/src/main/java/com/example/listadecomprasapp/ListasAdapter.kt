package com.example.listadecomprasapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load // <-- NOVO IMPORT (da biblioteca Coil)
import com.example.listadecomprasapp.databinding.ItemListaBinding

class ListasAdapter(
    private var listas: List<ListaDeCompras>,
    private val onItemClick: (ListaDeCompras) -> Unit,
    private val onItemLongClick: (ListaDeCompras) -> Unit
) : RecyclerView.Adapter<ListasAdapter.ListaViewHolder>() {

    inner class ListaViewHolder(private val binding: ItemListaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lista: ListaDeCompras) {
            binding.textViewNomeLista.text = lista.nome

            // --- 1. AQUI ESTÁ A MUDANÇA (RF003) ---
            if (lista.imageUrl != null) {
                // Se temos uma URL, usamos a Coil para carregar a imagem
                binding.imageViewLista.load(lista.imageUrl) {
                    crossfade(true) // Efeito suave de transição
                    placeholder(android.R.drawable.ic_menu_gallery) // Placeholder
                    error(android.R.drawable.ic_menu_close_clear_cancel) // Imagem de erro
                }
            } else {
                // Se não há imagem, mostrar placeholder
                binding.imageViewLista.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            // Lógica de cliques (sem mudanças)
            itemView.setOnClickListener {
                onItemClick(lista)
            }
            itemView.setOnLongClickListener {
                onItemLongClick(lista)
                true
            }
        }
    }

    // Função para o filtro da barra de busca (sem mudanças)
    fun atualizarListas(novaLista: List<ListaDeCompras>) {
        listas = novaLista
        notifyDataSetChanged()
    }

    // --- Funções Padrão (completas, sem mudanças) ---
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