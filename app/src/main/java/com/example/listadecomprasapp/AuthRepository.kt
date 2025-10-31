package com.example.listadecomprasapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

// Nosso "Gerente da Cozinha" de Autenticação
object AuthRepository {

    // A conexão direta com o "Estoque" (Firebase Auth)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Tenta fazer login de um usuário no Firebase.
     * A palavra 'suspend' significa que esta tarefa pode demorar e não
     * deve travar a tela do app.
     */
    suspend fun loginUsuario(email: String, senha: String): FirebaseUser? {
        // 'await()' espera a resposta do Firebase sem travar o app
        val task = auth.signInWithEmailAndPassword(email, senha).await()
        return task.user
    }

    /**
     * Tenta cadastrar um novo usuário no Firebase.
     */
    suspend fun cadastrarUsuario(email: String, senha: String): FirebaseUser? {
        val task = auth.createUserWithEmailAndPassword(email, senha).await()
        return task.user
    }

    // TODO: Adicionar as funções de logout e salvar nome de usuário no Firestore depois.
}