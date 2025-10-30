package com.example.listadecomprasapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.listadecomprasapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    // Declaração do ViewBinding
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Necessário chamar o super

        // Inflar o layout usando ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root) // Define o conteúdo da tela

        // Configurar o listener de clique para o botão "Acessar"
        binding.buttonAcessar.setOnClickListener {
            fazerLogin() // Chama a função de login
        }

        // Configurar o listener de clique para o texto "Criar Conta"
        binding.textViewCriarConta.setOnClickListener {
            // Cria a intenção de ir para a CadastroActivity
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent) // Inicia a nova atividade
        }
    }

    private fun fazerLogin() {
        // Obter o texto dos campos de email e senha
        val email = binding.editTextEmail.text.toString()
        val senha = binding.editTextSenha.text.toString()

        // Verificar se os campos estão vazios [cite: 68]
        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return // Para a execução se houver erro
        }
        // Verificar se o formato do email é válido [cite: 68]
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show()
            return // Para a execução se houver erro
        }

        // Procurar o usuário no nosso GerenciadorDeDados em memória
        val usuarioEncontrado = GerenciadorDeDados.encontrarUsuario(email, senha)

        // Verificar se o usuário foi encontrado
        if (usuarioEncontrado != null) {
            // --- Login bem-sucedido ---
            Toast.makeText(this, "Login bem-sucedido! Bem-vindo, ${usuarioEncontrado.nome}", Toast.LENGTH_SHORT).show()

            // Criar a intenção de ir para a SuasListasActivity
            val intent = Intent(this, SuasListasActivity::class.java)
            startActivity(intent)

            finish()

        } else {
            Toast.makeText(this, "E-mail ou senha incorretos.", Toast.LENGTH_SHORT).show()
        }
    }
}