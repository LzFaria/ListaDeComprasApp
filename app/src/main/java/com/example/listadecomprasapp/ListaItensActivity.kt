package com.example.listadecomprasapp // Seu pacote

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listadecomprasapp.databinding.ActivityListaItensBinding

class ListaItensActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaItensBinding
    private var nomeDaListaAtual: String? = null
    private var idDaListaAtual: String? = null
    private lateinit var adapter: ItensAdapter
    private var filtroAtual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Super aqui é necessário
        binding = ActivityListaItensBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nomeDaListaAtual = intent.getStringExtra("NOME_DA_LISTA")
        idDaListaAtual = intent.getStringExtra("LISTA_ID")

        if (nomeDaListaAtual == null || idDaListaAtual == null) {
            Toast.makeText(this, "Erro: Lista não encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.textViewTituloListaDinamico.text = nomeDaListaAtual

        binding.fabAdicionarItem.setOnClickListener {
            val intent = Intent(this, AdicionarItemActivity::class.java)
            intent.putExtra("NOME_DA_LISTA", nomeDaListaAtual)
            startActivity(intent)
        }

        binding.imageButtonEditList.setOnClickListener {
            abrirTelaDeEdicaoLista()
        }

        binding.recyclerViewItens.layoutManager = LinearLayoutManager(this)
        setupRecyclerView()

        binding.searchViewItens.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filtroAtual = newText ?: ""
                setupRecyclerView()
                return true
            }
        })
    }

    // --- onResume COM A LINHA super.onResume() ---
    override fun onResume() {
        super.onResume() // <-- GARANTA QUE ESTA LINHA ESTÁ AQUI E É A PRIMEIRA!

        // Código restante do onResume
        if (idDaListaAtual != null) {
            val lista = GerenciadorDeDados.encontrarListaPorId(idDaListaAtual!!)
            if (lista != null) {
                nomeDaListaAtual = lista.nome
                binding.textViewTituloListaDinamico.text = nomeDaListaAtual
            }
        }
        setupRecyclerView()
    }

    // --- Restante do código (setupRecyclerView, diálogos, etc. - sem mudanças) ---
    private fun setupRecyclerView() {
        if (nomeDaListaAtual == null) return

        val todosOsItensDaLista = GerenciadorDeDados.getItensDaLista(nomeDaListaAtual!!)

        val itensFiltrados = if (filtroAtual.isEmpty()) {
            todosOsItensDaLista
        } else {
            todosOsItensDaLista.filter { it.nome.contains(filtroAtual, ignoreCase = true) }
        }

        val listaOrdenada = itensFiltrados.sortedWith(
            compareBy<ItemDaLista> { it.comprado }
                .thenBy { it.categoria }
                .thenBy { it: ItemDaLista -> it.nome.lowercase() }
        )

        adapter = ItensAdapter(
            listaOrdenada,
            { item, isChecked -> // Checkbox
                item.comprado = isChecked
                setupRecyclerView()
            },
            { itemClicado -> // Clique Longo
                mostrarDialogoDeExclusao(itemClicado)
            },
            { itemClicado -> // Clique Simples
                abrirTelaDeEdicaoItem(itemClicado)
            }
        )

        binding.recyclerViewItens.adapter = adapter
    }

    private fun mostrarDialogoDeExclusao(item: ItemDaLista) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Item")
            .setMessage("Tem certeza que deseja excluir o item '${item.nome}'?")
            .setPositiveButton("Excluir") { dialog, _ ->
                GerenciadorDeDados.removerItemPorId(item.id)
                setupRecyclerView()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun abrirTelaDeEdicaoItem(item: ItemDaLista) {
        val intent = Intent(this, AdicionarItemActivity::class.java)
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