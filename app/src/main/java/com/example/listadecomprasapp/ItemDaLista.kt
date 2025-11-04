package com.example.listadecomprasapp

import com.google.firebase.firestore.DocumentId

enum class Categoria(val nome: String) {
    FRUTA("Fruta"),
    VERDURA("Verdura"),
    CARNE("Carne"),
    OUTRO("Outro")
}

data class ItemDaLista(
    @DocumentId
    val id: String = "",

    var nome: String = "",

    val nome_busca: String = nome.lowercase(),

    var quantidade: String = "",
    var unidade: String = "",
    var categoria: String = Categoria.OUTRO.nome,
    var comprado: Boolean = false
)