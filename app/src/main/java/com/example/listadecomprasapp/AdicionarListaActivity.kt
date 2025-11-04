package com.example.listadecomprasapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.load
import com.example.listadecomprasapp.databinding.ActivityAdicionarListaBinding
import java.io.File

class AdicionarListaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarListaBinding
    private val viewModel: AdicionarListaViewModel by viewModels()

    // --- Variáveis de Estado ---
    private var novaImagemUriSelecionada: Uri? = null
    private var uriParaTirarFoto: Uri? = null
    private var listaAtual: ListaDeCompras? = null
    private var modoDeEdicao = false

    // --- DEFINIÇÃO DOS "LANÇADORES" (UMA ÚNICA VEZ) ---

    // Lançador da GALERIA
    private val selecionarImagemLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            novaImagemUriSelecionada = uri
            binding.imageViewPreview.setImageURI(uri)
        }
    }

    // Lançador da CÂMERA
    private val tirarFotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { sucesso: Boolean ->
        if (sucesso) {
            val uriDaFoto = uriParaTirarFoto
            if (uriDaFoto != null) {
                novaImagemUriSelecionada = uriDaFoto
                binding.imageViewPreview.setImageURI(uriDaFoto)
            } else {
                Toast.makeText(this, "Erro ao salvar foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Lançador de PERMISSÃO da Câmera
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            lancarCamera()
        } else {
            Toast.makeText(this, "Permissão da câmera necessária", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Funções do Ciclo de Vida ---

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarListaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observarViewModel()

        // Lógica de Modo Edição
        val listaId = intent.getStringExtra("LISTA_ID_PARA_EDITAR")
        if (listaId == null) {
            // Modo Adicionar
            modoDeEdicao = false
            binding.buttonAdicionarLista.text = "Adicionar"
        } else {
            // Modo Edição
            modoDeEdicao = true
            binding.buttonAdicionarLista.text = "Salvar Alterações"
            viewModel.carregarLista(listaId) // Pede ao Chef para carregar os dados
        }

        // Listeners de Clique
        binding.buttonAdicionarLista.setOnClickListener {
            salvarLista()
        }
        binding.imageViewPreview.setOnClickListener {
            mostrarDialogoEscolhaFoto()
        }
    }

    // --- Funções do ViewModel e UI ---

    private fun observarViewModel() {
        // Observa o "concluido"
        viewModel.concluido.observe(this) { concluido ->
            if (concluido) {
                Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // Observa o "error"
        viewModel.error.observe(this) { error ->
            if (error != null && error.isNotEmpty()) {
                Toast.makeText(this, "Erro: $error", Toast.LENGTH_LONG).show()
            }
        }

        // Observa o "loading"
        viewModel.loading.observe(this) { isLoading ->
            binding.buttonAdicionarLista.isEnabled = !isLoading
        }

        // Observa a lista que o Chef buscou para edição
        viewModel.listaParaEditar.observe(this) { lista ->
            if (lista != null) {
                listaAtual = lista
                preencherFormulario(lista)
            } else if (modoDeEdicao) {
                // Se estamos em modo de edição e a lista veio nula, é um erro
                Toast.makeText(this, "Erro ao carregar lista para edição", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun salvarLista() {
        val nomeDaLista = binding.editTextNomeLista.text.toString()

        if (nomeDaLista.isEmpty()) {
            Toast.makeText(this, "Por favor, insira um nome para a lista.", Toast.LENGTH_SHORT).show()
            return
        }

        if (modoDeEdicao && listaAtual != null) {
            // --- MODO EDIÇÃO ---
            viewModel.atualizarLista(
                id = listaAtual!!.id,
                novoNome = nomeDaLista,
                novaUriLocal = novaImagemUriSelecionada,
                urlImagemAntiga = listaAtual!!.imageUrl
            )
        } else {
            // --- MODO ADICIONAR ---
            viewModel.salvarLista(nomeDaLista, novaImagemUriSelecionada)
        }
    }

    private fun preencherFormulario(lista: ListaDeCompras) {
        binding.editTextNomeLista.setText(lista.nome)

        if (lista.imageUrl != null) {
            // Usa a Coil para carregar a URL do Firebase na ImageView
            binding.imageViewPreview.load(lista.imageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    // --- Funções da Câmera/Galeria ---

    private fun mostrarDialogoEscolhaFoto() {
        val opcoes = arrayOf("Tirar Foto", "Escolher da Galeria")
        AlertDialog.Builder(this)
            .setTitle("Escolher Imagem")
            .setItems(opcoes) { dialog, which ->
                when (which) {
                    0 -> checarPermissaoCamera()
                    1 -> selecionarImagemLauncher.launch("image/*")
                }
            }
            .create()
            .show()
    }

    private fun checarPermissaoCamera() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                lancarCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun lancarCamera() {
        val uriLocalSegura: Uri = createImageUri()
        uriParaTirarFoto = uriLocalSegura
        tirarFotoLauncher.launch(uriLocalSegura)
    }

    private fun createImageUri(): Uri {
        val file = File(applicationContext.cacheDir, "lista_foto_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            applicationContext,
            "${applicationContext.packageName}.provider",
            file
        )
    }
}