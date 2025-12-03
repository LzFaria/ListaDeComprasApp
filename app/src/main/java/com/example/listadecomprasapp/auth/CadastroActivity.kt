package com.example.listadecomprasapp.auth

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.listadecomprasapp.databinding.ActivityCadastroBinding
import com.example.listadecomprasapp.auth.CadastroViewModel

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding

    private val cadastroViewModel: CadastroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observarViewModel()

        // Configurar os botões
        binding.buttonCriar.setOnClickListener {
            fazerCadastro()
        }

        // (Lógica do seu botão "Voltar")
        binding.textViewVoltarLogin.setOnClickListener {
            finish() // Simplesmente fecha esta tela
        }
    }

    //Configura os "observadores"
    private fun observarViewModel() {
        // Observa o quadro de SUCESSO
        cadastroViewModel.cadastroResult.observe(this) { firebaseUser ->
            if (firebaseUser != null) {
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

    //Função chamada pelo clique do botão "Criar"
    private fun fazerCadastro() {
        val nome = binding.editTextNomeCompleto.text.toString()
        val email = binding.editTextEmailCadastro.text.toString()
        val senha = binding.editTextSenhaCadastro.text.toString()
        val confirmarSenha = binding.editTextConfirmarSenha.text.toString()

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

        cadastroViewModel.cadastrar(nome, email, senha)
    }
}