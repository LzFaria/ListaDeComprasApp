package com.example.listadecomprasapp

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listadecomprasapp.databinding.ActivitySuasListasBinding

class SuasListasActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuasListasBinding
    private lateinit var adapter: ListasAdapter
    private var filtroAtual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuasListasBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Botão Flutuante (+)
        binding.fabAdicionarLista.setOnClickListener {
            val intent = Intent(this, AdicionarListaActivity::class.java)
            startActivity(intent)
        }

        binding.imageButtonLogout.setOnClickListener {
            mostrarDialogoLogout()
        }

        // --- Configuração do RecyclerView ---
        setupRecyclerView()

        // --- Listener da Busca ---
        binding.searchViewListas.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    // --- setupRecyclerView (Com GridLayoutManager) ---
    private fun setupRecyclerView() {
        val todasAsListas = GerenciadorDeDados.listasDeCompras
        val listasFiltradas = if (filtroAtual.isEmpty()) {
            todasAsListas
        } else {
            todasAsListas.filter { it.nome.contains(filtroAtual, ignoreCase = true) }
        }
        val listasOrdenadas = listasFiltradas.sortedBy { it.nome }

        adapter = ListasAdapter(
            listasOrdenadas,
            { listaClicada -> // Clique Simples (Navegar)
                val intent = Intent(this, ListaItensActivity::class.java)
                // Envia os dados necessários para a próxima tela
                intent.putExtra("NOME_DA_LISTA", listaClicada.nome)
                intent.putExtra("LISTA_ID", listaClicada.id)
                startActivity(intent)
            },
            { listaClicada -> // Clique Longo (Editar/Excluir Lista)
                mostrarDialogoOpcoesLista(listaClicada)
            }
        )

        // Define o layout como uma GRADE (Grid) de 2 colunas
        binding.recyclerViewListas.layoutManager = GridLayoutManager(this, 2)

        binding.recyclerViewListas.adapter = adapter
    }

    // --- Funções do Diálogo

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
            .setMessage("Tem certeza que deseja excluir a lista '${lista.nome}'? TODOS os itens dentro dela também serão apagados.")
            .setPositiveButton("Excluir") { dialog, _ ->
                GerenciadorDeDados.removerItensDaLista(lista.nome)
                GerenciadorDeDados.removerListaPorId(lista.id)
                setupRecyclerView()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    // --- Função de Logout
    private fun mostrarDialogoLogout() {
        AlertDialog.Builder(this)
            .setTitle("Sair")
            .setMessage("Tem certeza que deseja sair?")
            .setPositiveButton("Sair") { dialog, _ ->
                GerenciadorDeDados.fazerLogout()
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