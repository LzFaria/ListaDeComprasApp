package com.example.listadecomprasapp

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels // <-- NOVO IMPORT
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listadecomprasapp.databinding.ActivityListaItensBinding

class ListaItensActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaItensBinding
    private var nomeDaListaAtual: String? = null
    private var idDaListaAtual: String? = null
    private lateinit var adapter: ItensAdapter

    // 1. MUDANÇA: Conexão com o novo "Chef"
    private val viewModel: ListaItensViewModel by viewModels()

    private var listaCompleta: List<ItemDaLista> = emptyList()

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

        // Configurar o RecyclerView
        setupRecyclerViewInicial()

        // 2. MUDANÇA: Configurar "Observadores"
        observarViewModel()

        // --- Configuração dos Botões ---
        binding.fabAdicionarItem.setOnClickListener {
            val intent = Intent(this, AdicionarItemActivity::class.java)
            // 3. MUDANÇA: Passar o ID da Lista (não o nome)
            intent.putExtra("LISTA_ID", idDaListaAtual)
            startActivity(intent)
        }

        binding.imageButtonEditList.setOnClickListener {
            abrirTelaDeEdicaoLista()
        }

        // --- Listener da Busca ---
        binding.searchViewItens.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarItens(newText)
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Atualiza o nome (caso tenha mudado na tela de edição)
        if (idDaListaAtual != null) {
            val lista = GerenciadorDeDados.encontrarListaPorId(idDaListaAtual!!) // (Este GerenciadorDeDados está errado, mas deixamos por enquanto)
            if (lista != null) {
                nomeDaListaAtual = lista.nome
                binding.textViewTituloListaDinamico.text = nomeDaListaAtual
            }
        }
        // 4. MUDANÇA: Pede ao "Chef" para carregar os itens
        if (idDaListaAtual != null) {
            viewModel.carregarItens(idDaListaAtual!!)
        }
    }

    private fun observarViewModel() {
        viewModel.itens.observe(this) { itens ->
            listaCompleta = itens
            adapter.atualizarItens(itens)
        }
        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }

    // 5. MUDANÇA: Apenas configura o adapter uma vez
    private fun setupRecyclerViewInicial() {
        adapter = ItensAdapter(
            emptyList(), // Começa vazio
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

    private fun filtrarItens(query: String?) {
        val listaFiltrada = if (query.isNullOrEmpty()) {
            listaCompleta
        } else {
            listaCompleta.filter { it.nome.contains(query, ignoreCase = true) }
        }
        adapter.atualizarItens(listaFiltrada)
    }

    private fun mostrarDialogoDeExclusao(item: ItemDaLista) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Item")
            .setMessage("Tem certeza que deseja excluir o item '${item.nome}'?")
            .setPositiveButton("Excluir") { dialog, _ ->
                // 6. MUDANÇA: Pede ao "Chef" para excluir
                viewModel.excluirItem(idDaListaAtual!!, item.id)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun abrirTelaDeEdicaoItem(item: ItemDaLista) {
        val intent = Intent(this, AdicionarItemActivity::class.java)
        // 7. MUDANÇA: Passa o ID da Lista E o ID do Item
        intent.putExtra("LISTA_ID", idDaListaAtual)
        intent.putExtra("ITEM_ID_PARA_EDITAR", item.id)
        startActivity(intent)
    }

    // (Esta função não muda)
    private fun abrirTelaDeEdicaoLista() { /* ... */ }
}