package com.example.listadecomprasapp.itens

import android.R
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.listadecomprasapp.databinding.ActivityAdicionarItemBinding

class AdicionarItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarItemBinding
    private val viewModel: AdicionarItemViewModel by viewModels()

    private var listaId: String? = null
    private var itemParaEditar: ItemDaLista? = null
    private var modoDeEdicao = false

    // Lista de opções para o Spinner de Categoria (baseado no Enum)
    private val categorias = Categoria.values().map { it.nome }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Chamar as funções para popular AMBOS os spinners
        setupSpinnerUnidades()
        setupSpinnerCategoria()

        observarViewModel()

        // Receber os IDs
        listaId = intent.getStringExtra("LISTA_ID")
        val itemId = intent.getStringExtra("ITEM_ID_PARA_EDITAR")

        if (listaId == null) {
            Toast.makeText(this, "Erro: ID da Lista não encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (itemId == null) {
            // Modo Adicionar
            modoDeEdicao = false
            binding.buttonAdicionarItem.text = "Adicionar Item"
        } else {
            // Modo Edição
            modoDeEdicao = true
            binding.buttonAdicionarItem.text = "Salvar Alterações"
            viewModel.carregarItem(listaId!!, itemId)
        }

        binding.buttonAdicionarItem.setOnClickListener {
            salvarItem()
        }
    }

    private fun observarViewModel() {
        viewModel.concluido.observe(this) {
            if (it) {
                Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        viewModel.error.observe(this) {
            if (it != null && it.isNotEmpty()) {
                Toast.makeText(this, "Erro: $it", Toast.LENGTH_LONG).show()
            }
        }
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBarAdicionarItem.visibility = View.VISIBLE
                binding.buttonAdicionarItem.isEnabled = false
                binding.buttonAdicionarItem.alpha = 0.5f
            } else {
                binding.progressBarAdicionarItem.visibility = View.GONE
                binding.buttonAdicionarItem.isEnabled = true
                binding.buttonAdicionarItem.alpha = 1.0f
            }
        }
        viewModel.itemParaEditar.observe(this) { item ->
            if (item != null) {
                itemParaEditar = item
                preencherFormulario(item)
            }
        }
    }

    private fun preencherFormulario(item: ItemDaLista) {
        binding.editTextNomeItem.setText(item.nome)
        binding.editTextQuantidade.setText(item.quantidade)
        selecionarItemDoSpinner(binding.spinnerUnidade, item.unidade)
        selecionarItemDoSpinner(binding.spinnerCategoria, item.categoria)
    }

    private fun salvarItem() {
        val nome = binding.editTextNomeItem.text.toString()
        val quantidade = binding.editTextQuantidade.text.toString()
        // Agora 'spinnerUnidade' tem um adapter, então 'selectedItem' não é nulo
        val unidade = binding.spinnerUnidade.selectedItem.toString()
        val categoria = binding.spinnerCategoria.selectedItem.toString()

        if (nome.isEmpty() || quantidade.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha nome e quantidade.", Toast.LENGTH_SHORT).show()
            return
        }

        if (modoDeEdicao && itemParaEditar != null) {
            // ATUALIZAR
            val itemAtualizado = itemParaEditar!!.copy(
                nome = nome,
                quantidade = quantidade,
                unidade = unidade,
                categoria = categoria
            )
            viewModel.atualizarItem(listaId!!, itemAtualizado)

        } else {
            // ADICIONAR
            val novoItem = ItemDaLista(
                nome = nome,
                quantidade = quantidade,
                unidade = unidade,
                categoria = categoria,
                comprado = false
            )
            viewModel.salvarItem(listaId!!, novoItem)
        }
    }
    private fun setupSpinnerUnidades() {
        val unidades = listOf("un", "kg", "g", "L", "mL", "pct")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, unidades)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerUnidade.adapter = adapter
    }

    private fun setupSpinnerCategoria() {
        val adapter = ArrayAdapter(
            this, R.layout.simple_spinner_item,
            categorias
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoria.adapter = adapter
    }

    private fun selecionarItemDoSpinner(spinner: Spinner, valor: String) {
        val adapter = spinner.adapter as ArrayAdapter<String>
        val posicao = adapter.getPosition(valor)
        if (posicao >= 0) {
            spinner.setSelection(posicao)
        }
    }
}