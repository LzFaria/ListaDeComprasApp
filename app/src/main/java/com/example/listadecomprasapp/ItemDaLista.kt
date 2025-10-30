package com.example.listadecomprasapp // Seu pacote

import java.util.UUID // <-- NOVO IMPORT

// 1. Definição das Categorias (sem mudanças)
enum class Categoria {
    FRUTA,
    VERDURA,
    CARNE,
    OUTRO
}

// 2. O "molde" (data class) do nosso item
data class ItemDaLista(
    // 2. NOVO CAMPO: ID Único para cada item
    val id: String = UUID.randomUUID().toString(), // Gera um ID aleatório

    val nomeDaListaPai: String,

    // 3. MUDANÇA: 'var' para permitir a edição
    var nome: String,
    var quantidade: String,
    var unidade: String,
    var categoria: Categoria,
    var comprado: Boolean = false
)