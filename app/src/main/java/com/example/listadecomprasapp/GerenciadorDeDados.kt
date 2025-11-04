package com.example.listadecomprasapp

import android.net.Uri

object GerenciadorDeDados {

    val listaDeUsuarios = mutableListOf<Usuario>()

    fun adicionarUsuario(usuario: Usuario) {
        listaDeUsuarios.add(usuario)
    }

    fun encontrarUsuario(email: String, senha: String): Usuario? {
        return listaDeUsuarios.find { it.email == email && it.senha == senha }
    }

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

    fun fazerLogout() {
        listasDeCompras.clear()
    }
}