package com.example.listadecomprasapp // Seu pacote

import android.net.Uri
import java.util.UUID // <-- NOVO IMPORT

data class ListaDeCompras(
    // 1. NOVO CAMPO: ID Único
    val id: String = UUID.randomUUID().toString(),

    // 2. MUDANÇA: 'val' para 'var' para permitir edição
    var nome: String,
    var imagemUri: Uri?
)