package com.example.listadecomprasapp

import com.google.firebase.firestore.DocumentId

// 1. Convertendo o Enum para String (mais fácil de salvar no Firebase)
// (Poderíamos manter o Enum, mas String é mais simples)
enum class Categoria(val nome: String) {
    FRUTA("Fruta"),
    VERDURA("Verdura"),
    CARNE("Carne"),
    OUTRO("Outro")
}

// O "molde" do Item para o Firestore
data class ItemDaLista(
    @DocumentId
    val id: String = "", // O Firestore vai preencher

    // Não precisamos mais do 'nomeDaListaPai', pois o item
    // viverá DENTRO do documento da lista-pai (em uma sub-coleção)

    var nome: String = "",
    var quantidade: String = "",
    var unidade: String = "",

    // Salvamos o nome da categoria como String
    var categoria: String = Categoria.OUTRO.nome,

    var comprado: Boolean = false
)