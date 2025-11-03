package com.example.listadecomprasapp

import android.net.Uri

object GerenciadorDeDados {

    // --- Parte do Usuário (Ainda usado pelo Login antigo, se houver) ---
    val listaDeUsuarios = mutableListOf<Usuario>()

    fun adicionarUsuario(usuario: Usuario) {
        listaDeUsuarios.add(usuario)
    }

    fun encontrarUsuario(email: String, senha: String): Usuario? {
        return listaDeUsuarios.find { it.email == email && it.senha == senha }
    }

    // --- Parte das Listas de Compras (Ainda usado pela Edição antiga) ---
    val listasDeCompras = mutableListOf<ListaDeCompras>()

    fun adicionarLista(lista: ListaDeCompras) {
        listasDeCompras.add(lista)
    }

    fun encontrarListaPorId(id: String): ListaDeCompras? {
        return listasDeCompras.find { it.id == id }
    }

    fun removerListaPorId(id: String) {
        listasDeCompras.removeAll { it.id == id }
    }

    // --- FUNÇÕES DE ITENS REMOVIDAS ---
    // A lógica de itens (todosOsItens, adicionarItem, getItensDaLista)
    // foi removida daqui, pois agora é gerenciada pelo ListasRepository
    // e estava causando os erros de build 'nomeDaListaPai'.

    /**
     * Limpa os dados da sessão (listas e itens) ao fazer logout.
     */
    fun fazerLogout() {
        listasDeCompras.clear()
        // todosOsItens.clear() // Removido
    }
}