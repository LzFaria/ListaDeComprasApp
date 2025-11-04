package com.example.listadecomprasapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

// Nosso "Gerente da Cozinha" de Autenticação e Dados do Usuário
object AuthRepository {

    // O "Estoque" de Autenticação
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // O "Estoque" de Dados (Firestore)
    private val db = Firebase.firestore

    /**
     * Tenta fazer login de um usuário no Firebase. (Sem mudanças)
     */
    suspend fun loginUsuario(email: String, senha: String): FirebaseUser? {
        val task = auth.signInWithEmailAndPassword(email, senha).await()
        return task.user
    }

    /**
     * Tenta cadastrar um novo usuário no Firebase Auth. (Sem mudanças)
     */
    suspend fun cadastrarUsuario(email: String, senha: String): FirebaseUser? {
        val task = auth.createUserWithEmailAndPassword(email, senha).await()
        return task.user
    }

    /**
     * RF002: Salva o nome do usuário no Firestore. (Sem mudanças)
     */
    suspend fun salvarNomeUsuario(user: FirebaseUser, nome: String) {
        val dadosDoUsuario = hashMapOf(
            "nome" to nome,
            "email" to user.email
        )
        db.collection("usuarios")
            .document(user.uid)
            .set(dadosDoUsuario)
            .await()
    }

    // --- 1. NOVAS FUNÇÕES ---

    /**
     * RF001: Envia um e-mail de recuperação de senha.
     */
    suspend fun recuperarSenha(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    /**
     * RF001: Faz o logout do usuário atual do Firebase.
     */
    fun fazerLogout() {
        auth.signOut()
    }
}