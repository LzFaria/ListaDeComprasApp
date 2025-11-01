package com.example.listadecomprasapp

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.listadecomprasapp.databinding.ActivitySuasListasBinding

class SuasListasActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuasListasBinding
    private lateinit var adapter: ListasAdapter

    // Conexão com o "Chef" (sem mudanças)
    private val listasViewModel: ListasViewModel by viewModels()

    private var listaCompleta: List<ListaDeCompras> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuasListasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewListas.layoutManager = GridLayoutManager(this, 2)

        observarViewModel()

        // Configuração dos Botões (sem mudanças)
        binding.fabAdicionarLista.setOnClickListener {
            val intent = Intent(this, AdicionarListaActivity::class.java)
            startActivity(intent)
        }
        binding.imageButtonLogout.setOnClickListener {
            mostrarDialogoLogout()
        }

        // Listener da Busca (sem mudanças)
        binding.searchViewListas.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarListas(newText)
                return true
            }
        })
    }

    /**
     * O Garçom fica de olho nos quadros de aviso do Chef (sem mudanças)
     */
    private fun observarViewModel() {
        listasViewModel.listas.observe(this) { listas ->
            listaCompleta = listas

            // 1. MUDANÇA: Passamos a lista para o adapter
            // (Se o adapter já existir, apenas atualizamos)
            if (::adapter.isInitialized) {
                adapter.atualizarListas(listas)
            } else {
                adapter = ListasAdapter(
                    listas,
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
                binding.recyclerViewListas.adapter = adapter
            }
        }

        listasViewModel.error.observe(this) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        listasViewModel.carregarListas() // (Sem mudanças)
    }

    /**
     * Lógica da barra de busca (Sem mudanças)
     */
    private fun filtrarListas(query: String?) {
        val listaFiltrada = if (query.isNullOrEmpty()) {
            listaCompleta
        } else {
            listaCompleta.filter {
                it.nome.contains(query, ignoreCase = true)
            }
        }

        // Se o adapter já foi criado, apenas atualiza
        if (::adapter.isInitialized) {
            adapter.atualizarListas(listaFiltrada)
        }
    }

    // --- Funções de Diálogo (COM MUDANÇAS) ---

    // (Esta função não muda)
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

    // (Esta função não muda)
    private fun abrirTelaDeEdicaoLista(lista: ListaDeCompras) {
        val intent = Intent(this, AdicionarListaActivity::class.java)
        intent.putExtra("LISTA_ID_PARA_EDITAR", lista.id)
        startActivity(intent)
    }

    /**
     * 2. MUDANÇA CRUCIAL:
     * O diálogo de confirmação agora chama o 'ViewModel'
     * em vez do 'GerenciadorDeDados'.
     */
    private fun confirmarExclusaoLista(lista: ListaDeCompras) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Lista")
            .setMessage("Tem certeza que deseja excluir a lista '${lista.nome}'?")
            .setPositiveButton("Excluir") { dialog, _ ->

                // MUDOU AQUI:
                // Em vez de 'GerenciadorDeDados.remover...',
                // nós pedimos ao "Chef" (ViewModel) para excluir.
                listasViewModel.excluirLista(lista)

                // Não precisamos mais do 'setupRecyclerView()' aqui,
                // pois o ViewModel vai recarregar a lista
                // e o "observador" (observe) vai atualizar a tela!

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    // (Esta função não muda)
    private fun mostrarDialogoLogout() {
        AlertDialog.Builder(this)
            .setTitle("Sair")
            .setMessage("Tem certeza que deseja sair?")
            .setPositiveButton("Sair") { dialog, _ ->
                // TODO: Chamar o AuthRepository.logout()
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