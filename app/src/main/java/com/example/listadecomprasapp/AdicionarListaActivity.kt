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
import com.example.listadecomprasapp.databinding.ActivityAdicionarListaBinding
import java.io.File

class AdicionarListaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarListaBinding

    // Conexão com o "Chef"
    private val viewModel: AdicionarListaViewModel by viewModels()

    // Variáveis para guardar o estado da tela
    private var imagemUriSelecionada: Uri? = null
    private var uriParaTirarFoto: Uri? = null
    private var listaParaEditar: ListaDeCompras? = null
    private var modoDeEdicao = false

    // --- DEFINIÇÃO CORRETA DOS "LANÇADORES" (NO TOPO DA CLASSE) ---

    // 1. Lançador da GALERIA
    private val selecionarImagemLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Este bloco roda quando o usuário escolhe uma imagem
        if (uri != null) {
            imagemUriSelecionada = uri
            binding.imageViewPreview.setImageURI(uri)
        }
    }

    // 2. Lançador da CÂMERA
    private val tirarFotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { sucesso: Boolean ->
        // Este bloco roda quando o usuário tira a foto
        if (sucesso) {
            val uriDaFoto = uriParaTirarFoto
            if (uriDaFoto != null) {
                imagemUriSelecionada = uriDaFoto
                binding.imageViewPreview.setImageURI(uriDaFoto)
            } else {
                Toast.makeText(this, "Erro ao salvar foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 3. Lançador de PERMISSÃO da Câmera
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Este bloco roda quando o usuário permite ou nega
        if (isGranted) {
            lancarCamera()
        } else {
            Toast.makeText(this, "Permissão da câmera necessária", Toast.LENGTH_SHORT).show()
        }
    }

    // -----------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarListaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observarViewModel()

        // Lógica do Modo Edição (Ainda não implementada 100%)
        val listaId = intent.getStringExtra("LISTA_ID_PARA_EDITAR")
        if (listaId == null) {
            modoDeEdicao = false
            binding.buttonAdicionarLista.text = "Adicionar"
        } else {
            modoDeEdicao = true
            // TODO: Buscar dados do ViewModel para preencher
            // listaParaEditar = GerenciadorDeDados.encontrarListaPorId(listaId) // (Código antigo, vamos refatorar)
            binding.buttonAdicionarLista.text = "Salvar Alterações"
            // preencherFormulario(listaParaEditar!!) // (Código antigo, vamos refatorar)
        }

        // --- Lógica de Cliques ---
        binding.buttonAdicionarLista.setOnClickListener {
            salvarLista()
        }

        binding.imageViewPreview.setOnClickListener {
            mostrarDialogoEscolhaFoto()
        }
    }

    /**
     * O Garçom (Activity) fica de olho nos quadros de aviso do Chef
     */
    private fun observarViewModel() {
        // Observa o quadro "concluido"
        viewModel.concluido.observe(this) { concluido ->
            if (concluido) {
                Toast.makeText(this, "Lista salva com sucesso!", Toast.LENGTH_SHORT).show()
                finish() // Fecha a tela e volta para a lista
            }
        }

        // Observa o quadro "error"
        viewModel.error.observe(this) { error ->
            if (error != null && error.isNotEmpty()) {
                Toast.makeText(this, "Erro: $error", Toast.LENGTH_LONG).show()
            }
        }

        // Observa o quadro "loading"
        viewModel.loading.observe(this) { isLoading ->
            // Mostra um "Carregando..." (ProgressBar) ou desabilita o botão
            binding.buttonAdicionarLista.isEnabled = !isLoading
            // (Opcional: podemos adicionar um ProgressBar visível)
        }
    }

    /**
     * O Garçom (Activity) entrega o pedido para o Chef (ViewModel)
     */
    private fun salvarLista() {
        val nomeDaLista = binding.editTextNomeLista.text.toString()

        if (nomeDaLista.isEmpty()) {
            Toast.makeText(this, "Por favor, insira um nome para a lista.", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Implementar a lógica de Edição
        if (modoDeEdicao) {
            Toast.makeText(this, "Função Editar ainda será implementada.", Toast.LENGTH_SHORT).show()
            // viewModel.atualizarLista(...)
        } else {
            // Entrega o pedido ao Chef (ViewModel)
            viewModel.salvarLista(nomeDaLista, imagemUriSelecionada)
        }
    }

    // --- Funções da Câmera/Galeria (Completas e Corretas) ---

    private fun mostrarDialogoEscolhaFoto() {
        val opcoes = arrayOf("Tirar Foto", "Escolher da Galeria")
        AlertDialog.Builder(this)
            .setTitle("Escolher Imagem")
            .setItems(opcoes) { dialog, which ->
                when (which) {
                    0 -> checarPermissaoCamera()
                    // Esta é a linha que deu erro: 'launch' agora existe!
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

    private fun preencherFormulario(lista: ListaDeCompras) {
        // (Código antigo, vamos refatorar quando fizermos a Edição)
        binding.editTextNomeLista.setText(lista.nome)

        lista.imageUrl?.let { urlDaImagem ->
            // TODO: Usar Glide/Coil para carregar a URL
            binding.imageViewPreview.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }
}