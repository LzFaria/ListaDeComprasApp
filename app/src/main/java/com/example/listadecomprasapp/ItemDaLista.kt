package com.example.listadecomprasapp

import com.google.firebase.firestore.DocumentId

// O Enum (molde) para as categorias (sem mudanças)
enum class Categoria(val nome: String) {
    FRUTA("Fruta"),
    VERDURA("Verdura"),
    CARNE("Carne"),
    OUTRO("Outro")
}

// O "molde" do Item para o Firestore
data class ItemDaLista(
    @DocumentId
    val id: String = "", // O Firestore preenche

    var nome: String = "",

    // 1. NOVO CAMPO (RF005)
    val nome_busca: String = nome.lowercase(),

    var quantidade: String = "",
    var unidade: String = "",
    var categoria: String = Categoria.OUTRO.nome,
    var comprado: Boolean = false
)