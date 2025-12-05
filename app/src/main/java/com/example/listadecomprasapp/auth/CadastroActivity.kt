package com.example.listadecomprasapp.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.listadecomprasapp.databinding.ActivityCadastroBinding

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding

    private val cadastroViewModel: CadastroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observarViewModel()

        binding.buttonCriar.setOnClickListener {
            fazerCadastro()
        }

        binding.textViewVoltarLogin.setOnClickListener {
            finish()
        }
    }

    private fun observarViewModel() {
        cadastroViewModel.cadastroResult.observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        cadastroViewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        cadastroViewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBarCadastro.visibility = View.VISIBLE
                binding.buttonCriar.isEnabled = false
                binding.textViewVoltarLogin.isEnabled = false
                binding.cadastroContainer.alpha = 0.5f
            } else {
                binding.progressBarCadastro.visibility = View.GONE
                binding.buttonCriar.isEnabled = true
                binding.textViewVoltarLogin.isEnabled = true
                binding.cadastroContainer.alpha = 1.0f
            }
        }
    }

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