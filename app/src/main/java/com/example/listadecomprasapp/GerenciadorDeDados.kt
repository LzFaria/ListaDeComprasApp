package com.example.listadecomprasapp

import android.net.Uri
import java.util.UUID

object GerenciadorDeDados {

    // Usuário
    val listaDeUsuarios = mutableListOf<Usuario>()

    fun adicionarUsuario(usuario: Usuario) {
        listaDeUsuarios.add(usuario)
    }

    fun encontrarUsuario(email: String, senha: String): Usuario? {
        return listaDeUsuarios.find { it.email == email && it.senha == senha }
    }

    // Listas de Compras
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

    // Itens da Lista
    val todosOsItens = mutableListOf<ItemDaLista>()

    fun adicionarItem(item: ItemDaLista) {
        todosOsItens.add(item)
    }

    fun getItensDaLista(nomeDaLista: String): List<ItemDaLista> {
        return todosOsItens.filter { it.nomeDaListaPai == nomeDaLista }
    }

    fun removerItemPorId(id: String) {
        todosOsItens.removeAll { it.id == id }
    }

    fun encontrarItemPorId(id: String): ItemDaLista? {
        return todosOsItens.find { it.id == id }
    }

    fun removerItensDaLista(nomeDaListaPai: String) {
        todosOsItens.removeAll { it.nomeDaListaPai == nomeDaListaPai }
    }

    fun fazerLogout() {
        listasDeCompras.clear()
        todosOsItens.clear()
    }
}