package com.example.listadecomprasapp.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val db = Firebase.firestore

    suspend fun loginUsuario(email: String, senha: String): FirebaseUser? {
        val task = auth.signInWithEmailAndPassword(email, senha).await()
        return task.user
    }

    suspend fun cadastrarUsuario(email: String, senha: String): FirebaseUser? {
        val task = auth.createUserWithEmailAndPassword(email, senha).await()
        return task.user
    }

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

    suspend fun recuperarSenha(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun fazerLogout() {
        auth.signOut()
    }
}