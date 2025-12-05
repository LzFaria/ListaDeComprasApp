package com.example.listadecomprasapp.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.listadecomprasapp.databinding.ActivityLoginBinding
import com.example.listadecomprasapp.listas.SuasListasActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAcessar.setOnClickListener {
            fazerLogin()
        }
        binding.textViewCriarConta.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        binding.textViewRecuperarSenha.setOnClickListener {
            mostrarDialogoRecuperarSenha()
        }

        observarViewModel()
    }

    private fun observarViewModel() {
        loginViewModel.loginResult.observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SuasListasActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        loginViewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
        loginViewModel.resetEnviado.observe(this) { enviado ->
            if (enviado) {
                Toast.makeText(this, "E-mail de recuperação enviado! Verifique sua caixa de entrada.", Toast.LENGTH_LONG).show()
            }
        }
        loginViewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBarLogin.visibility = View.VISIBLE
                binding.buttonAcessar.isEnabled = false
                binding.textViewCriarConta.isEnabled = false
                binding.textViewRecuperarSenha.isEnabled = false
                binding.loginContainer.alpha = 0.5f
            } else {
                binding.progressBarLogin.visibility = View.GONE
                binding.buttonAcessar.isEnabled = true
                binding.textViewCriarConta.isEnabled = true
                binding.textViewRecuperarSenha.isEnabled = true
                binding.loginContainer.alpha = 1.0f
            }
        }
    }

    private fun fazerLogin() {
        val email = binding.editTextEmail.text.toString()
        val senha = binding.editTextSenha.text.toString()
        if (email.isEmpty() || senha.isEmpty()) { /* ... */ }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { /* ... */ }

        loginViewModel.login(email, senha)
    }

    private fun mostrarDialogoRecuperarSenha() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Recuperar Senha")
        builder.setMessage("Por favor, insira seu e-mail para receber o link de recuperação:")

        val input = EditText(this)
        input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        builder.setView(input)

        builder.setPositiveButton("Enviar") { dialog, _ ->
            val email = input.text.toString()
            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                loginViewModel.recuperarSenha(email)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}