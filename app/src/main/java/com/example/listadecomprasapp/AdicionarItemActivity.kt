package com.example.listadecomprasapp // Seu pacote

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.listadecomprasapp.databinding.ActivityAdicionarItemBinding

class AdicionarItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarItemBinding
    private var nomeDaListaPai: String? = null
    private var itemParaEditar: ItemDaLista? = null
    private var modoDeEdicao = false

    // Lista de opções para o Spinner de Categoria
    private val categorias = listOf("Fruta", "Verdura", "Carne", "Outro")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar os dois Spinners
        setupSpinnerUnidades()
        setupSpinnerCategoria()

        // --- Verificar o Modo (Adicionar vs Editar) ---
        val itemId = intent.getStringExtra("ITEM_ID_PARA_EDITAR")

        if (itemId == null) {
            // Modo Adicionar
            modoDeEdicao = false
            nomeDaListaPai = intent.getStringExtra("NOME_DA_LISTA")
            title = "Adicionar Item"
            binding.buttonAdicionarItem.text = "Adicionar Item"
        } else {
            // Modo Edição
            modoDeEdicao = true
            itemParaEditar = GerenciadorDeDados.encontrarItemPorId(itemId)
            if (itemParaEditar == null) {
                Toast.makeText(this, "Erro: Item não encontrado.", Toast.LENGTH_LONG).show()
                finish()
                return
            }
            title = "Editar Item"
            binding.buttonAdicionarItem.text = "Salvar Alterações"
            preencherFormulario(itemParaEditar!!)
        }

        binding.buttonAdicionarItem.setOnClickListener {
            salvarItem()
        }
    }
    private fun setupSpinnerCategoria() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categorias
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoria.adapter = adapter
    }

    //Preenche o formulário com os dados de um item existente (Modo Edição)/
    private fun preencherFormulario(item: ItemDaLista) {
        binding.editTextNomeItem.setText(item.nome)
        binding.editTextQuantidade.setText(item.quantidade)

        selecionarItemDoSpinner(binding.spinnerUnidade, item.unidade)

        // MUDANÇA: Seleciona a categoria correta no novo Spinner
        val nomeCategoria = when (item.categoria) {
            Categoria.FRUTA -> "Fruta"
            Categoria.VERDURA -> "Verdura"
            Categoria.CARNE -> "Carne"
            Categoria.OUTRO -> "Outro"
        }
        selecionarItemDoSpinner(binding.spinnerCategoria, nomeCategoria)
    }

    //Função que salva (adicionando ou atualizando) o item
    private fun salvarItem() {
        val nome = binding.editTextNomeItem.text.toString()
        val quantidade = binding.editTextQuantidade.text.toString()
        val unidade = binding.spinnerUnidade.selectedItem.toString()

        //Lê a categoria do novo Spinner
        val categoria = when (binding.spinnerCategoria.selectedItem.toString()) {
            "Fruta" -> Categoria.FRUTA
            "Verdura" -> Categoria.VERDURA
            "Carne" -> Categoria.CARNE
            else -> Categoria.OUTRO
        }

        // Validações
        if (nome.isEmpty() || quantidade.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha nome e quantidade.", Toast.LENGTH_SHORT).show()
            return
        }

        // Decidir se vai Atualizar ou Adicionar
        if (modoDeEdicao && itemParaEditar != null) {
            // ATUALIZAR
            itemParaEditar!!.nome = nome
            itemParaEditar!!.quantidade = quantidade
            itemParaEditar!!.unidade = unidade
            itemParaEditar!!.categoria = categoria
            Toast.makeText(this, "Item atualizado!", Toast.LENGTH_SHORT).show()

        } else if (nomeDaListaPai != null) {
            // ADICIONAR
            val novoItem = ItemDaLista(
                nomeDaListaPai = nomeDaListaPai!!,
                nome = nome,
                quantidade = quantidade,
                unidade = unidade,
                categoria = categoria
            )
            GerenciadorDeDados.adicionarItem(novoItem)
            Toast.makeText(this, "'$nome' adicionado!", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    // Funções Auxiliares (Spinner - Sem mudanças)
    private fun setupSpinnerUnidades() {
        val unidades = listOf("un", "kg", "g", "L", "mL", "pct")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, unidades)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUnidade.adapter = adapter
    }

    private fun selecionarItemDoSpinner(spinner: Spinner, valor: String) {
        // Esta função serve para AMBOS os spinners!
        val adapter = spinner.adapter as ArrayAdapter<String>
        val posicao = adapter.getPosition(valor)
        if (posicao >= 0) {
            spinner.setSelection(posicao)
        }
    }
}