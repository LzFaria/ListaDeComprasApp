package com.example.listadecomprasapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText // <-- NOVO IMPORT
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog // <-- NOVO IMPORT
import androidx.appcompat.app.AppCompatActivity
import com.example.listadecomprasapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listeners dos botões
        binding.buttonAcessar.setOnClickListener {
            fazerLogin()
        }
        binding.textViewCriarConta.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        // --- 1. NOVO LISTENER (RF001) ---
        binding.textViewRecuperarSenha.setOnClickListener {
            mostrarDialogoRecuperarSenha()
        }

        observarViewModel()
    }

    /**
     * 2. MUDANÇA: Agora também observa o 'resetEnviado'
     */
    private fun observarViewModel() {
        // Observa o sucesso do LOGIN
        loginViewModel.loginResult.observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SuasListasActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Observa o ERRO (para login ou recuperação)
        loginViewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        // --- 3. NOVO OBSERVADOR (RF001) ---
        // Observa o sucesso da RECUPERAÇÃO DE SENHA
        loginViewModel.resetEnviado.observe(this) { enviado ->
            if (enviado) {
                Toast.makeText(this, "E-mail de recuperação enviado! Verifique sua caixa de entrada.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fazerLogin() {
        // ... (Lógica de validação do login, sem mudanças)
        val email = binding.editTextEmail.text.toString()
        val senha = binding.editTextSenha.text.toString()
        if (email.isEmpty() || senha.isEmpty()) { /* ... */ }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { /* ... */ }

        // Chama o Chef (sem mudanças)
        loginViewModel.login(email, senha)
    }

    // --- 4. NOVA FUNÇÃO (RF001) ---
    /**
     * Mostra um pop-up (AlertDialog) pedindo o e-mail do usuário
     * para iniciar a recuperação de senha.
     */
    private fun mostrarDialogoRecuperarSenha() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Recuperar Senha")
        builder.setMessage("Por favor, insira seu e-mail para receber o link de recuperação:")

        // Adiciona um campo de texto (EditText) dentro do pop-up
        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        builder.setView(input)

        // Configura o botão "Enviar"
        builder.setPositiveButton("Enviar") { dialog, _ ->
            val email = input.text.toString()
            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Se o e-mail for válido, pede ao "Chef" (ViewModel)
                loginViewModel.recuperarSenha(email)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura o botão "Cancelar"
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}