package com.example.listadecomprasapp // Seu pacote

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.listadecomprasapp.databinding.ActivityAdicionarListaBinding
import java.io.File

class AdicionarListaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarListaBinding

    // Variável de classe para guardar a Uri (pode ser nula)
    private var imagemUriSelecionada: Uri? = null
    // Variável de classe para guardar a Uri da câmera temporariamente
    private var uriParaTirarFoto: Uri? = null

    private var listaParaEditar: ListaDeCompras? = null
    private var modoDeEdicao = false

    // Lançador da GALERIA
    private val selecionarImagemLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imagemUriSelecionada = uri
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
                imagemUriSelecionada = uriDaFoto
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarListaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Lógica de Modo Edição ---
        val listaId = intent.getStringExtra("LISTA_ID_PARA_EDITAR")
        if (listaId == null) {
            modoDeEdicao = false
            binding.buttonAdicionarLista.text = "Adicionar"
        } else {
            modoDeEdicao = true
            listaParaEditar = GerenciadorDeDados.encontrarListaPorId(listaId)
            if (listaParaEditar == null) {
                Toast.makeText(this, "Erro: Lista não encontrada.", Toast.LENGTH_LONG).show()
                finish()
                return
            }
            binding.buttonAdicionarLista.text = "Salvar Alterações"
            preencherFormulario(listaParaEditar!!)
        }

        // --- Lógica de Cliques ---
        binding.buttonAdicionarLista.setOnClickListener {
            salvarLista()
        }

        binding.imageViewPreview.setOnClickListener {
            mostrarDialogoEscolhaFoto()
        }
    }

    // --- Funções da Câmera e Galeria ---
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
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
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

    // --- AQUI ESTÁ A CORREÇÃO ---
    private fun lancarCamera() {
        // 1. Criamos uma Uri local (não-nula)
        val uriLocalSegura: Uri = createImageUri()

        // 2. Guardamos ela na nossa variável de classe (que é nula)
        //    para usar no 'callback' do 'tirarFotoLauncher'
        uriParaTirarFoto = uriLocalSegura

        // 3. Lançamos a câmera usando a Uri local (não-nula)
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

    // --- Funções de Preencher e Salvar ---

    private fun preencherFormulario(lista: ListaDeCompras) {
        binding.editTextNomeLista.setText(lista.nome)

        lista.imagemUri?.let { uriDaImagem ->
            imagemUriSelecionada = uriDaImagem
            binding.imageViewPreview.setImageURI(uriDaImagem)
        }
    }

    private fun salvarLista() {
        val nomeDaLista = binding.editTextNomeLista.text.toString()

        if (nomeDaLista.isEmpty()) {
            Toast.makeText(this, "Por favor, insira um nome para a lista.", Toast.LENGTH_SHORT).show()
            return
        }

        if (modoDeEdicao && listaParaEditar != null) {
            listaParaEditar!!.nome = nomeDaLista
            listaParaEditar!!.imagemUri = imagemUriSelecionada
            Toast.makeText(this, "Lista atualizada!", Toast.LENGTH_SHORT).show()

        } else {
            val novaLista = ListaDeCompras(
                nome = nomeDaLista,
                imagemUri = imagemUriSelecionada
            )
            GerenciadorDeDados.adicionarLista(novaLista)
            Toast.makeText(this, "Lista '$nomeDaLista' adicionada!", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}