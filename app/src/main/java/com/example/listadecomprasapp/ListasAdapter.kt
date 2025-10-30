package com.example.listadecomprasapp // Seu pacote

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecomprasapp.databinding.ItemListaBinding

// Adicionado 'onItemLongClick'
class ListasAdapter(
    private val listas: List<ListaDeCompras>,
    private val onItemClick: (ListaDeCompras) -> Unit, // Para Navegação
    private val onItemLongClick: (ListaDeCompras) -> Unit // <-- NOVO (Para Editar/Excluir)
) : RecyclerView.Adapter<ListasAdapter.ListaViewHolder>() {

    inner class ListaViewHolder(private val binding: ItemListaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lista: ListaDeCompras) {
            binding.textViewNomeLista.text = lista.nome

            if (lista.imagemUri != null) {
                binding.imageViewLista.setImageURI(lista.imagemUri)
            } else {
                binding.imageViewLista.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            // Configurar os dois cliques

            // Clique Simples (Navegar para a lista de itens)
            itemView.setOnClickListener {
                onItemClick(lista)
            }

            // Clique Longo (Abrir diálogo de Editar/Excluir)
            itemView.setOnLongClickListener {
                onItemLongClick(lista)
                true // Consome o clique
            }
        }
    }

    // --- Funções Padrão (completas) ---
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