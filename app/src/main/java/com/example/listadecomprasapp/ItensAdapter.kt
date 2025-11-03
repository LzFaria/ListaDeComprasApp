package com.example.listadecomprasapp

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecomprasapp.databinding.ItemItemListaBinding

class ItensAdapter(
    // 1. MUDANÇA: 'itens' agora é uma 'var' para atualização
    private var itens: List<ItemDaLista>,
    private val onItemCheckedChange: (ItemDaLista, Boolean) -> Unit,
    private val onItemLongClick: (ItemDaLista) -> Unit,
    private val onItemClick: (ItemDaLista) -> Unit
) : RecyclerView.Adapter<ItensAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val binding: ItemItemListaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemDaLista) {
            binding.textViewNomeItem.text = item.nome
            binding.textViewQtdUnidade.text = "${item.quantidade} ${item.unidade}"

            // 2. MUDANÇA: Lidar com o 'comprado' e listeners
            // Removemos o listener antigo para evitar loops
            binding.checkBoxComprado.setOnCheckedChangeListener(null)
            binding.checkBoxComprado.isChecked = item.comprado

            // 3. MUDANÇA: Define o ícone com base no NOME (String) da categoria
            val icone = when (item.categoria) {
                Categoria.FRUTA.nome -> R.drawable.ic_fruta
                Categoria.VERDURA.nome -> R.drawable.ic_verdura
                Categoria.CARNE.nome -> R.drawable.ic_carne
                else -> R.drawable.ic_outro
            }
            binding.imageViewIconeCategoria.setImageResource(icone)

            atualizarVisualizacaoComprado(item.comprado)

            // 4. MUDANÇA: Novo listener para o CheckBox
            binding.checkBoxComprado.setOnCheckedChangeListener { _, isChecked ->
                onItemCheckedChange(item, isChecked)
            }

            // Listeners de clique (sem mudanças)
            itemView.setOnLongClickListener {
                onItemLongClick(item)
                true
            }
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }

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

    // 5. NOVA FUNÇÃO: Para atualizar a lista (ex: filtro)
    fun atualizarItens(novosItens: List<ItemDaLista>) {
        this.itens = novosItens
        notifyDataSetChanged()
    }

    // --- Funções Padrão (completas) ---
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