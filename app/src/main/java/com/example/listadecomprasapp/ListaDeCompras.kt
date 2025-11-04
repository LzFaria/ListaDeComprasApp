package com.example.listadecomprasapp

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

// O "molde" para o Firestore
data class ListaDeCompras(
    @DocumentId
    val id: String = "", // O Firestore preenche

    var nome: String = "",

    // 1. NOVO CAMPO (RF005)
    // Este campo salva uma cópia do 'nome' em minúsculo.
    // O 'nome.lowercase()' garante que ele seja criado automaticamente.
    val nome_busca: String = nome.lowercase(),

    val userId: String = "",

    var imageUrl: String? = null
) {
    // @Exclude diz ao Firestore para ignorar este campo
    @get:Exclude
    var imagemUriLocal: android.net.Uri? = null
}