package com.example.listadecomprasapp

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listadecomprasapp.databinding.ActivityListaItensBinding

class ListaItensActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaItensBinding
    private var nomeDaListaAtual: String? = null
    private var idDaListaAtual: String? = null
    private lateinit var adapter: ItensAdapter
    private val viewModel: ListaItensViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaItensBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nomeDaListaAtual = intent.getStringExtra("NOME_DA_LISTA")
        idDaListaAtual = intent.getStringExtra("LISTA_ID")

        if (nomeDaListaAtual == null || idDaListaAtual == null) {
            Toast.makeText(this, "Erro: Lista não encontrada", Toast.LENGTH_SHORT).show()
            finish();
            return
        }

        binding.textViewTituloListaDinamico.text = nomeDaListaAtual

        setupRecyclerViewInicial()
        observarViewModel()

        // --- 1. CORREÇÃO AQUI (Preenchendo os listeners) ---
        binding.fabAdicionarItem.setOnClickListener {
            val intent = Intent(this, AdicionarItemActivity::class.java)
            intent.putExtra("LISTA_ID", idDaListaAtual)
            startActivity(intent)
        }
        binding.imageButtonEditList.setOnClickListener {
            abrirTelaDeEdicaoLista()
        }

        // --- Listener da Busca (Correto) ---
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
        // Atualiza o nome da lista (caso tenha mudado na tela de edição)
        if (idDaListaAtual != null) {
            // TODO: Esta lógica ainda usa GerenciadorDeDados,
            // precisamos refatorar 'abrirTelaDeEdicaoLista'
            val lista = GerenciadorDeDados.encontrarListaPorId(idDaListaAtual!!)
            if (lista != null) {
                nomeDaListaAtual = lista.nome
                binding.textViewTituloListaDinamico.text = nomeDaListaAtual
            }
        }
        binding.searchViewItens.setQuery("", false)
        viewModel.carregarItens(idDaListaAtual!!)
    }

    private fun observarViewModel() {
        viewModel.itens.observe(this) { itens ->
            adapter.atualizarItens(itens)
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
            { item, isChecked -> // Checkbox
                viewModel.atualizarItemComprado(idDaListaAtual!!, item, isChecked)
            },
            { itemClicado -> // Clique Longo
                mostrarDialogoDeExclusao(itemClicado)
            },
            { itemClicado -> // Clique Simples
                abrirTelaDeEdicaoItem(itemClicado)
            }
        )
        binding.recyclerViewItens.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewItens.adapter = adapter
    }

    // --- Funções de Diálogo e Navegação (100% COMPLETAS) ---
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