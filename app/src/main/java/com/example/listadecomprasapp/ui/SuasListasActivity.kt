package com.example.listadecomprasapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.listadecomprasapp.ui.AdicionarListaActivity
import com.example.listadecomprasapp.data.ListaDeCompras
import com.example.listadecomprasapp.ui.ListaItensActivity
import com.example.listadecomprasapp.adapter.ListasAdapter
import com.example.listadecomprasapp.viewmodel.ListasViewModel
import com.example.listadecomprasapp.databinding.ActivitySuasListasBinding

class SuasListasActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuasListasBinding
    private lateinit var adapter: ListasAdapter
    private val listasViewModel: ListasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySuasListasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViewInicial()
        observarViewModel()

        binding.fabAdicionarLista.setOnClickListener {
            val intent = Intent(this, AdicionarListaActivity::class.java)
            startActivity(intent)
        }
        binding.imageButtonLogout.setOnClickListener {
            mostrarDialogoLogout()
        }

        // --- Listener da Busca ---
        binding.searchViewListas.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                listasViewModel.carregarListas(newText ?: "")
                return true
            }
        })
    }

    private fun observarViewModel() {
        listasViewModel.listas.observe(this) { listas ->
            if (::adapter.isInitialized) {
                adapter.atualizarListas(listas)
            }
        }
        listasViewModel.error.observe(this) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume() // Esta também é obrigatória
        binding.searchViewListas.setQuery("", false)
        listasViewModel.carregarListas()
    }

    private fun setupRecyclerViewInicial() {
        adapter = ListasAdapter(
            emptyList(),
            { listaClicada -> // Clique Simples (Navegar)
                val intent = Intent(this, ListaItensActivity::class.java)
                intent.putExtra("NOME_DA_LISTA", listaClicada.nome)
                intent.putExtra("LISTA_ID", listaClicada.id)
                startActivity(intent)
            },
            { listaClicada -> // Clique Longo (Editar/Excluir Lista)
                mostrarDialogoOpcoesLista(listaClicada)
            }
        )
        binding.recyclerViewListas.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewListas.adapter = adapter
    }

    // --- Funções de Diálogo e Logout

    private fun mostrarDialogoOpcoesLista(lista: ListaDeCompras) {
        val opcoes = arrayOf("Editar", "Excluir")
        AlertDialog.Builder(this)
            .setTitle(lista.nome)
            .setItems(opcoes) { dialog, which ->
                when (which) {
                    0 -> abrirTelaDeEdicaoLista(lista)
                    1 -> confirmarExclusaoLista(lista)
                }
            }
            .create()
            .show()
    }

    private fun abrirTelaDeEdicaoLista(lista: ListaDeCompras) {
        val intent = Intent(this, AdicionarListaActivity::class.java)
        intent.putExtra("LISTA_ID_PARA_EDITAR", lista.id)
        startActivity(intent)
    }

    private fun confirmarExclusaoLista(lista: ListaDeCompras) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Lista")
            .setMessage("Tem certeza que deseja excluir a lista '${lista.nome}'?")
            .setPositiveButton("Excluir") { dialog, _ ->
                listasViewModel.excluirLista(lista)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun mostrarDialogoLogout() {
        AlertDialog.Builder(this)
            .setTitle("Sair")
            .setMessage("Tem certeza que deseja sair?")
            .setPositiveButton("Sair") { dialog, _ ->

                listasViewModel.fazerLogout()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}