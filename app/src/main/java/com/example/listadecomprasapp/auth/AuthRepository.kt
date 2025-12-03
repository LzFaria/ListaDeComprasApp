package com.example.listadecomprasapp.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val db = Firebase.firestore

    //Tenta fazer login de um usuário no Firebase.
    suspend fun loginUsuario(email: String, senha: String): FirebaseUser? {
        val task = auth.signInWithEmailAndPassword(email, senha).await()
        return task.user
    }

    // Tenta cadastrar um novo usuário no Firebase Auth.
    suspend fun cadastrarUsuario(email: String, senha: String): FirebaseUser? {
        val task = auth.createUserWithEmailAndPassword(email, senha).await()
        return task.user
    }

    //RF002: Salva o nome do usuário no Firestore.
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

    //Envia um e-mail de recuperação de senha.
    suspend fun recuperarSenha(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    //Faz o logout do usuário atual do Firebase.
    fun fazerLogout() {
        auth.signOut()
    }
}