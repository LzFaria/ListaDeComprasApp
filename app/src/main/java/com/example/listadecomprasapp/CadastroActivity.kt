package com.example.listadecomprasapp

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels // <-- NOVO IMPORT
import androidx.appcompat.app.AppCompatActivity
import com.example.listadecomprasapp.databinding.ActivityCadastroBinding

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding

    // 1. O Garçom (Activity) agora tem o contato do Chef (ViewModel)
    private val cadastroViewModel: CadastroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Configurar o Garçom para "observar" os quadros de aviso do Chef
        observarViewModel()

        // Configurar os botões
        binding.buttonCriar.setOnClickListener {
            fazerCadastro()
        }

        // (Lógica do seu botão "Voltar" que você tinha adicionado)
        binding.textViewVoltarLogin.setOnClickListener {
            finish() // Simplesmente fecha esta tela
        }
    }

    /**
     * Configura os "observadores"
     */
    private fun observarViewModel() {
        // Observa o quadro de SUCESSO
        cadastroViewModel.cadastroResult.observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                // O Chef avisou que o cadastro deu certo!
                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()
                finish() // Fecha a tela de Cadastro e volta pro Login
            }
        }

        // Observa o quadro de ERRO
        cadastroViewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                // O Chef avisou que deu um problema!
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Função chamada pelo clique do botão "Criar"
     */
    private fun fazerCadastro() {
        // 1. Coletar os dados (igual a antes)
        val nome = binding.editTextNomeCompleto.text.toString()
        val email = binding.editTextEmailCadastro.text.toString()
        val senha = binding.editTextSenhaCadastro.text.toString()
        val confirmarSenha = binding.editTextConfirmarSenha.text.toString()

        // 2. Validações locais (RF002) - igual a antes
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Formato de e-mail inválido.", Toast.LENGTH_SHORT).show()
            return
        }
        if (senha != confirmarSenha) {
            Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
            return
        }
        if (senha.length < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. MUDANÇA CRUCIAL:
        // O Garçom entrega o pedido ao Chef.
        // REMOVEMOS: a chamada para GerenciadorDeDados.adicionarUsuario(...)
        cadastroViewModel.cadastrar(nome, email, senha)
    }
}