package com.example.listadecomprasapp

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels // <-- NOVO IMPORT
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.listadecomprasapp.databinding.ActivitySuasListasBinding

class SuasListasActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuasListasBinding
    private lateinit var adapter: ListasAdapter

    // 1. O Garçom agora tem o contato do Chef de Listas!
    private val listasViewModel: ListasViewModel by viewModels()

    // 2. Guarda a lista completa (para o filtro de busca)
    private var listaCompleta: List<ListaDeCompras> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuasListasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar o RecyclerView (o LayoutManager)
        // Usamos a grade de 2 colunas que você pediu!
        binding.recyclerViewListas.layoutManager = GridLayoutManager(this, 2)

        // 3. Configurar os "Observadores"
        observarViewModel()

        // --- Configuração dos Botões (sem mudanças) ---
        binding.fabAdicionarLista.setOnClickListener {
            val intent = Intent(this, AdicionarListaActivity::class.java)
            startActivity(intent)
        }
        binding.imageButtonLogout.setOnClickListener {
            mostrarDialogoLogout()
        }

        // --- Listener da Busca (sem mudanças na configuração) ---
        binding.searchViewListas.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarListas(newText)
                return true
            }
        })
    }

    /**
     * O Garçom fica de olho nos quadros de aviso do Chef
     */
    private fun observarViewModel() {
        // Observa o quadro de SUCESSO (a lista de listas)
        listasViewModel.listas.observe(this) { listas ->
            // 1. Guarda a lista completa
            listaCompleta = listas
            // 2. Atualiza o "manual" (Adapter) com a lista
            adapter = ListasAdapter(
                listas,
                { listaClicada -> // Clique Simples (Navegar)
                    // TODO: Atualizar esta lógica quando o RF004 for refatorado
                    val intent = Intent(this, ListaItensActivity::class.java)
                    intent.putExtra("NOME_DA_LISTA", listaClicada.nome)
                    intent.putExtra("LISTA_ID", listaClicada.id)
                    startActivity(intent)
                },
                { listaClicada -> // Clique Longo (Editar/Excluir Lista)
                    // TODO: Atualizar esta lógica para o Firebase
                    mostrarDialogoOpcoesLista(listaClicada)
                }
            )
            // 3. Conecta o manual ao RecyclerView
            binding.recyclerViewListas.adapter = adapter
        }

        // Observa o quadro de ERRO
        listasViewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * O Garçom (Activity) chama o Chef (ViewModel) para
     * carregar os dados toda vez que a tela volta ao foco.
     */
    override fun onResume() {
        super.onResume()
        // Pede ao Chef para carregar as listas do Estoque (Firebase)
        listasViewModel.carregarListas()
    }

    /**
     * Lógica da barra de busca (RF005)
     * Agora ela filtra a 'listaCompleta' que veio do ViewModel
     */
    private fun filtrarListas(query: String?) {
        val listaFiltrada = if (query.isNullOrEmpty()) {
            listaCompleta
        } else {
            listaCompleta.filter {
                it.nome.contains(query, ignoreCase = true)
            }
        }
        // Atualiza o adapter com os dados filtrados
        adapter.atualizarListas(listaFiltrada)
    }

    // --- Funções de Diálogo e Logout (SEM MUDANÇAS POR ENQUANTO) ---
    // (Ainda usam o GerenciadorDeDados, vamos refatorar depois)

    private fun mostrarDialogoOpcoesLista(lista: ListaDeCompras) {
        // ... (código existente)
    }
    private fun abrirTelaDeEdicaoLista(lista: ListaDeCompras) {
        // ... (código existente)
    }
    private fun confirmarExclusaoLista(lista: ListaDeCompras) {
        // ... (código existente)
    }
    private fun mostrarDialogoLogout() {
        // ... (código existente)
        // (Este diálogo NÃO precisa de mudança, já está bom)
    }
}