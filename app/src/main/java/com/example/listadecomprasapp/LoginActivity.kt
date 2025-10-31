package com.example.listadecomprasapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.listadecomprasapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // 1. O Garçom agora tem uma conexão direta com o Chef!
    // 'by viewModels()' é a forma moderna e fácil de obter o ViewModel.
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

        // 2. O Garçom começa a "observar" os quadros de aviso do Chef
        observarViewModel()
    }

    /**
     * Esta função configura os "observadores". O Garçom fica de olho
     * nos quadros de aviso para saber quando o prato (resultado) está pronto.
     */
    private fun observarViewModel() {
        // Observa o quadro de SUCESSO
        loginViewModel.loginResult.observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                // O Chef avisou que o login deu certo!
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()

                // Navegar para a tela principal
                val intent = Intent(this, SuasListasActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Observa o quadro de ERRO
        loginViewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                // O Chef avisou que deu um problema!
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fazerLogin() {
        val email = binding.editTextEmail.text.toString()
        val senha = binding.editTextSenha.text.toString()

        // Validações (continuam iguais)
        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. MUDANÇA CRUCIAL:
        // O Garçom não vai mais ao estoque! Ele entrega o pedido ao Chef.
        // REMOVEMOS: a chamada para GerenciadorDeDados.encontrarUsuario(...)
        loginViewModel.login(email, senha)
    }
}