package com.example.listadecomprasapp

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

    private var imagemUriSelecionada: Uri? = null
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
            imagemUriSelecionada = uriParaTirarFoto
            binding.imageViewPreview.setImageURI(imagemUriSelecionada)
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

        // Lógica de Modo Edição
        val listaId = intent.getStringExtra("LISTA_ID_PARA_EDITAR")
        if (listaId == null) {
            modoDeEdicao = false
            // title = "Adicionar Lista"
            binding.buttonAdicionarLista.text = "Adicionar"
        } else {
            modoDeEdicao = true
            listaParaEditar = GerenciadorDeDados.encontrarListaPorId(listaId)
            if (listaParaEditar == null) {
                Toast.makeText(this, "Erro: Lista não encontrada.", Toast.LENGTH_LONG).show()
                finish()
                return
            }
            // title = "Editar Lista"
            binding.buttonAdicionarLista.text = "Salvar Alterações"
            preencherFormulario(listaParaEditar!!)
        }

        // Lógica de Cliques
        binding.buttonAdicionarLista.setOnClickListener {
            salvarLista()
        }

        binding.imageViewPreview.setOnClickListener {
            mostrarDialogoEscolhaFoto()
        }
    }

    // Funções da Câmera e Galeria (Completas)
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
                // TODO: Mostrar diálogo explicativo (opcional)
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun lancarCamera() {
        uriParaTirarFoto = createImageUri()
        tirarFotoLauncher.launch(uriParaTirarFoto)
    }

    private fun createImageUri(): Uri {
        val file = File(applicationContext.cacheDir, "lista_foto_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            applicationContext,
            "${applicationContext.packageName}.provider",
            file
        )
    }

    // Funções de Preencher e Salvar (Completas)

    private fun preencherFormulario(lista: ListaDeCompras) {
        binding.editTextNomeLista.setText(lista.nome)
        if (lista.imagemUri != null) {
            imagemUriSelecionada = lista.imagemUri
            binding.imageViewPreview.setImageURI(lista.imagemUri)
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