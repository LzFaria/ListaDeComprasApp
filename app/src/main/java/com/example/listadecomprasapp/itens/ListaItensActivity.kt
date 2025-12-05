package com.example.listadecomprasapp.itens

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listadecomprasapp.databinding.ActivityListaItensBinding
import com.example.listadecomprasapp.listas.AdicionarListaActivity

class ListaItensActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaItensBinding
    private var idDaListaAtual: String? = null
    private lateinit var adapter: ItensAdapter
    private val viewModel: ListaItensViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaItensBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idDaListaAtual = intent.getStringExtra("LISTA_ID")

        if (idDaListaAtual == null) {
            Toast.makeText(this, "Erro: ID da Lista não encontrado", Toast.LENGTH_SHORT).show()
            finish();
            return
        }

        setupRecyclerViewInicial()
        observarViewModel()

        binding.fabAdicionarItem.setOnClickListener {
            val intent = Intent(this, AdicionarItemActivity::class.java)
            intent.putExtra("LISTA_ID", idDaListaAtual)
            startActivity(intent)
        }

        binding.imageButtonEditList.setOnClickListener {
            abrirTelaDeEdicaoLista()
        }

        binding.searchViewItens.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.carregarItens(idDaListaAtual!!, newText ?: "")
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (idDaListaAtual == null) return

        viewModel.carregarNomeDaLista(idDaListaAtual!!)
        viewModel.carregarItens(idDaListaAtual!!)

        binding.searchViewItens.setQuery("", false)
    }

    private fun observarViewModel() {
        viewModel.itens.observe(this) { itens ->
            adapter.atualizarItens(itens)
        }

        viewModel.nomeDaLista.observe(this) { nome ->
            binding.textViewTituloListaDinamico.text = nome
        }

        viewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupRecyclerViewInicial() {
        adapter = ItensAdapter(
            emptyList(),
            { item, isChecked ->
                viewModel.atualizarItemComprado(idDaListaAtual!!, item, isChecked)
            },
            { itemClicado ->
                mostrarDialogoDeExclusao(itemClicado)
            },
            { itemClicado ->
                abrirTelaDeEdicaoItem(itemClicado)
            }
        )
        binding.recyclerViewItens.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewItens.adapter = adapter
    }

    private fun mostrarDialogoDeExclusao(item: ItemDaLista) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Item")
            .setMessage("Tem certeza que deseja excluir o item '${item.nome}'?")
            .setPositiveButton("Excluir") { dialog, _ ->
                viewModel.excluirItem(idDaListaAtual!!, item.id)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun abrirTelaDeEdicaoItem(item: ItemDaLista) {
        val intent = Intent(this, AdicionarItemActivity::class.java)
        intent.putExtra("LISTA_ID", idDaListaAtual)
        intent.putExtra("ITEM_ID_PARA_EDITAR", item.id)
        startActivity(intent)
    }

    private fun abrirTelaDeEdicaoLista() {
        if (idDaListaAtual == null) return
        val intent = Intent(this, AdicionarListaActivity::class.java)
        intent.putExtra("LISTA_ID_PARA_EDITAR", idDaListaAtual)
        startActivity(intent)
    }
}