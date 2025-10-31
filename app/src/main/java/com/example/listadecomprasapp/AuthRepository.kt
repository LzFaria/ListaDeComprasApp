package com.example.listadecomprasapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore // <-- NOVO IMPORT
import com.google.firebase.ktx.Firebase // <-- NOVO IMPORT
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
     * RF002: Salva o nome e email do usuário no Firestore,
     * usando o UID do usuário como chave do documento.
     */
    suspend fun salvarNomeUsuario(user: FirebaseUser, nome: String) {
        // 1. Criar o "molde" dos dados (um Mapa)
        val dadosDoUsuario = hashMapOf(
            "nome" to nome,
            "email" to user.email
        )

        // 2. Salvar no Firestore:
        // Vá até a coleção "usuarios"
        // Crie/Acesse um documento com o ID (uid) do usuário
        // E salve (set) os 'dadosDoUsuario' dentro dele
        db.collection("usuarios")
            .document(user.uid)
            .set(dadosDoUsuario)
            .await() // Espera a operação terminar
    }

    // TODO: Adicionar o logout depois
}