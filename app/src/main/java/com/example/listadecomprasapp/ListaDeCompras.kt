package com.example.listadecomprasapp

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.util.UUID

data class ListaDeCompras(
    @DocumentId
    val id: String = "",

    // 1. CORREÇÃO: 'val' mudou para 'var' para permitir a edição (RF003)
    var nome: String = "",

    val userId: String = "",

    // 'imageUrl' é o que salvamos no Firestore
    var imageUrl: String? = null
) {
    // 'imagemUriLocal' é a Uri temporária da câmera/galeria
    @get:Exclude
    var imagemUriLocal: android.net.Uri? = null
}