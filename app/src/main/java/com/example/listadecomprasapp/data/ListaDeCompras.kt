package com.example.listadecomprasapp.data

import android.net.Uri
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class ListaDeCompras(
    @DocumentId
    val id: String = "",

    var nome: String = "",

    val nome_busca: String = nome.lowercase(),

    val userId: String = "",

    var imageUrl: String? = null
) {
    @get:Exclude
    var imagemUriLocal: Uri? = null
}