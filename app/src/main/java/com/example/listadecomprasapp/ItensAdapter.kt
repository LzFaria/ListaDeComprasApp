package com.example.listadecomprasapp // Seu pacote

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecomprasapp.databinding.ItemItemListaBinding

class ItensAdapter(
    private val itens: List<ItemDaLista>,
    private val onItemCheckedChange: (ItemDaLista, Boolean) -> Unit,
    private val onItemLongClick: (ItemDaLista) -> Unit, // Para Excluir
    private val onItemClick: (ItemDaLista) -> Unit      // Para Editar
) : RecyclerView.Adapter<ItensAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val binding: ItemItemListaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemDaLista) {
            // --- Código de bind ---
            binding.textViewNomeItem.text = item.nome
            binding.textViewQtdUnidade.text = "${item.quantidade} ${item.unidade}"
            binding.checkBoxComprado.isChecked = item.comprado

            val icone = when (item.categoria) {
                Categoria.FRUTA -> R.drawable.ic_fruta
                Categoria.VERDURA -> R.drawable.ic_verdura
                Categoria.CARNE -> R.drawable.ic_carne
                Categoria.OUTRO -> R.drawable.ic_outro
            }
            binding.imageViewIconeCategoria.setImageResource(icone)

            atualizarVisualizacaoComprado(item.comprado)

            // --- Listeners ---

            // CheckBox
            binding.checkBoxComprado.setOnCheckedChangeListener { _, isChecked ->
                atualizarVisualizacaoComprado(isChecked)
                onItemCheckedChange(item, isChecked)
            }

            // Clique Longo (Excluir)
            itemView.setOnLongClickListener {
                onItemLongClick(item)
                true
            }

            // Clique Simples (Editar)
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }

        // Função interna para riscar o texto
        private fun atualizarVisualizacaoComprado(comprado: Boolean) {
            if (comprado) {
                binding.textViewNomeItem.paintFlags = binding.textViewNomeItem.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.textViewQtdUnidade.paintFlags = binding.textViewQtdUnidade.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.textViewNomeItem.paintFlags = binding.textViewNomeItem.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.textViewQtdUnidade.paintFlags = binding.textViewQtdUnidade.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
    }

    // --- Funções Padrão do Adapter (AGORA COMPLETAS) ---
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemItemListaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itens[position])
    }

    override fun getItemCount(): Int {
        return itens.size
    }
}