package com.example.listadecomprasapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecomprasapp.databinding.ItemListaBinding

// 1. MUDANÇA: 'listas' agora é uma 'var' (variável) para podermos filtrar
class ListasAdapter(
    private var listas: List<ListaDeCompras>, // Agora é 'var'
    private val onItemClick: (ListaDeCompras) -> Unit,
    private val onItemLongClick: (ListaDeCompras) -> Unit
) : RecyclerView.Adapter<ListasAdapter.ListaViewHolder>() {

    inner class ListaViewHolder(private val binding: ItemListaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lista: ListaDeCompras) {
            binding.textViewNomeLista.text = lista.nome

            // 2. MUDANÇA: Lógica da Imagem
            // Por enquanto, o 'lista.imageUrl' está vazio (não adicionamos nada).
            // Vamos apenas mostrar o placeholder (RF003).
            // No próximo passo (Adicionar Lista), vamos implementar o Glide/Coil
            // para carregar a 'lista.imageUrl' aqui.
            if (lista.imageUrl != null) {
                // TODO: Carregar imagem da URL com Glide/Coil
                binding.imageViewLista.setImageResource(android.R.drawable.ic_menu_gallery) // Placeholder
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

    // 3. NOVA FUNÇÃO: Para o filtro da barra de busca
    fun atualizarListas(novaLista: List<ListaDeCompras>) {
        listas = novaLista
        notifyDataSetChanged() // Avisa o RecyclerView para se redesenhar
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