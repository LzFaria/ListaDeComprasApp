package com.example.listadecomprasapp.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecomprasapp.R
import com.example.listadecomprasapp.data.Categoria
import com.example.listadecomprasapp.data.ItemDaLista
import com.example.listadecomprasapp.databinding.ItemItemListaBinding

class ItensAdapter(
    private var itens: List<ItemDaLista>,
    private val onItemCheckedChange: (ItemDaLista, Boolean) -> Unit,
    private val onItemLongClick: (ItemDaLista) -> Unit,
    private val onItemClick: (ItemDaLista) -> Unit
) : RecyclerView.Adapter<ItensAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val binding: ItemItemListaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemDaLista) {
            binding.textViewNomeItem.text = item.nome
            binding.textViewQtdUnidade.text = "${item.quantidade} ${item.unidade}"

            binding.checkBoxComprado.setOnCheckedChangeListener(null)
            binding.checkBoxComprado.isChecked = item.comprado

            val icone = when (item.categoria) {
                Categoria.FRUTA.nome -> R.drawable.ic_fruta
                Categoria.VERDURA.nome -> R.drawable.ic_verdura
                Categoria.CARNE.nome -> R.drawable.ic_carne
                else -> R.drawable.ic_outro
            }
            binding.imageViewIconeCategoria.setImageResource(icone)

            atualizarVisualizacaoComprado(item.comprado)

            binding.checkBoxComprado.setOnCheckedChangeListener { _, isChecked ->
                onItemCheckedChange(item, isChecked)
            }

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

    fun atualizarItens(novosItens: List<ItemDaLista>) {
        this.itens = novosItens
        notifyDataSetChanged()
    }

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