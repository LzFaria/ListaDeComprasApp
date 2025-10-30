package com.example.listadecomprasapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.listadecomprasapp.databinding.ActivityCadastroBinding // Importante!

class CadastroActivity : AppCompatActivity() {

    // Declaração do ViewBinding
    private lateinit var binding: ActivityCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar o layout
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        // Definir o conteúdo da tela
        setContentView(binding.root)

        binding.buttonCriar.setOnClickListener {
            fazerCadastro()
        }

        binding.textViewVoltarLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fazerCadastro() {
        // Coletar os dados dos campos
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

        // Se todas as validações passaram:

        // Criar um novo objeto Usuario com os dados
        val novoUsuario = Usuario(nome = nome, email = email, senha = senha)

        // Adicionar o usuário ao nosso gerenciador global
        GerenciadorDeDados.adicionarUsuario(novoUsuario)

        // Informar o sucesso e fechar a tela
        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()

        // (Opcional, mas útil para debugar) Imprimir no console para ver a lista crescendo
        println("Usuários cadastrados: ${GerenciadorDeDados.listaDeUsuarios}")

        finish() // Fecha a tela de Cadastro e volta pro Login
    }
}