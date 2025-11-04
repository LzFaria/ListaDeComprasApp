package com.example.listadecomprasapp

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query // <-- IMPORTANTE
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

object ListasRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val storage = Firebase.storage

    private fun getUserId(): String? = auth.currentUser?.uid

    // --- Funções de LISTAS (Com Lógica de Busca Corrigida) ---

    suspend fun getMinhasListas(filtroDeBusca: String = ""): List<ListaDeCompras> {
        val userId = getUserId() ?: return emptyList()
        try {
            // 1. CORREÇÃO: Declaramos 'query' como tipo 'Query'
            //    e fazemos a primeira filtragem (que já tínhamos)
            var query: Query = db.collection("listas")
                .whereEqualTo("userId", userId)

            if (filtroDeBusca.isNotEmpty()) {
                // --- 2. LÓGICA DE BUSCA COM FILTRO (RF005) ---
                val filtroMinusculo = filtroDeBusca.lowercase()
                query = query.whereGreaterThanOrEqualTo("nome_busca", filtroMinusculo)
                    .whereLessThanOrEqualTo("nome_busca", filtroMinusculo + '\uf8ff')
                    // A primeira ordenação DEVE ser no campo do filtro
                    .orderBy("nome_busca")
            } else {
                // --- 3. LÓGICA DE BUSCA SEM FILTRO (A ANTIGA, RF003) ---
                query = query.orderBy("nome")
            }

            val task = query.get().await()
            return task.toObjects(ListaDeCompras::class.java)
        } catch (e: Exception) {
            println("Erro ao buscar listas: ${e.message}")
            return emptyList()
        }
    }

    // --- Funções de ITENS (Com Lógica de Busca Corrigida) ---

    suspend fun getItensDaLista(listaId: String, filtroDeBusca: String = ""): List<ItemDaLista> {
        try {
            // 1. CORREÇÃO: Declaramos 'query' como tipo 'Query'
            var query: Query = getCaminhoItens(listaId)

            if (filtroDeBusca.isNotEmpty()) {
                // --- 2. LÓGICA DE BUSCA COM FILTRO (RF005) ---
                val filtroMinusculo = filtroDeBusca.lowercase()
                query = query.whereGreaterThanOrEqualTo("nome_busca", filtroMinusculo)
                    .whereLessThanOrEqualTo("nome_busca", filtroMinusculo + '\uf8ff')
                    // A primeira ordenação DEVE ser no campo do filtro
                    .orderBy("nome_busca")
            } else {
                // --- 3. LÓGICA DE BUSCA SEM FILTRO (A ANTIGA, RF004) ---
                query = query.orderBy("comprado")
                    .orderBy("categoria")
                    .orderBy("nome")
            }

            val task = query.get().await()
            return task.toObjects(ItemDaLista::class.java)
        } catch (e: Exception) {
            println("Erro ao buscar itens: ${e.message}")
            return emptyList()
        }
    }

    // --- DEMAIS FUNÇÕES (100% COMPLETAS, SEM ATALHOS) ---

    private suspend fun uploadImagemLista(uri: Uri): String {
        val userId = getUserId() ?: "unknown"
        val fileName = "listas/${userId}_${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(fileName)
        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }

    suspend fun adicionarLista(nome: String, uriLocal: Uri?) {
        val userId = getUserId() ?: return
        var downloadUrl: String? = null
        if (uriLocal != null) {
            downloadUrl = uploadImagemLista(uriLocal)
        }
        val novaLista = ListaDeCompras(
            nome = nome,
            userId = userId,
            imageUrl = downloadUrl
        )
        db.collection("listas").add(novaLista).await()
    }

    suspend fun excluirLista(lista: ListaDeCompras) {
        excluirSubcolecaoItens(lista.id)
        val urlDaImagem = lista.imageUrl
        if (urlDaImagem != null) {
            try {
                val storageRef = storage.getReferenceFromUrl(urlDaImagem)
                storageRef.delete().await()
            } catch (e: Exception) {
                println("Erro ao deletar imagem do Storage: ${e.message}")
            }
        }
        db.collection("listas").document(lista.id).delete().await()
    }

    private suspend fun excluirSubcolecaoItens(listaId: String) {
        val snapshot = getCaminhoItens(listaId).get().await()
        val batch = db.batch()
        for (documento in snapshot.documents) {
            batch.delete(documento.reference)
        }
        batch.commit().await()
    }

    private suspend fun excluirImagemAnterior(imageUrl: String?) {
        if (imageUrl == null) return
        try {
            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.delete().await()
        } catch (e: Exception) {
            println("Erro ao deletar imagem anterior: ${e.message}")
        }
    }

    suspend fun getListaPorId(id: String): ListaDeCompras? {
        try {
            val doc = db.collection("listas").document(id).get().await()
            return doc.toObject(ListaDeCompras::class.java)
        } catch (e: Exception) {
            println("Erro ao buscar lista por ID: ${e.message}")
            return null
        }
    }

    suspend fun atualizarLista(
        id: String,
        novoNome: String,
        novaUriLocal: Uri?,
        urlImagemAntiga: String?
    ) {
        var novaUrlImagem = urlImagemAntiga
        if (novaUriLocal != null) {
            novaUrlImagem = uploadImagemLista(novaUriLocal)
            excluirImagemAnterior(urlImagemAntiga)
        }

        val dadosAtualizados = mapOf(
            "nome" to novoNome,
            "nome_busca" to novoNome.lowercase(),
            "imageUrl" to novaUrlImagem
        )

        db.collection("listas").document(id).update(dadosAtualizados).await()
    }

    private fun getCaminhoItens(listaId: String) =
        db.collection("listas").document(listaId).collection("itens")

    suspend fun adicionarItem(listaId: String, item: ItemDaLista) {
        getCaminhoItens(listaId).add(item).await()
    }

    suspend fun getItemPorId(listaId: String, itemId: String): ItemDaLista? {
        try {
            val doc = getCaminhoItens(listaId).document(itemId).get().await()
            return doc.toObject(ItemDaLista::class.java)
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun atualizarItem(listaId: String, item: ItemDaLista) {
        getCaminhoItens(listaId).document(item.id).set(item).await()
    }

    suspend fun excluirItem(listaId: String, itemId: String) {
        getCaminhoItens(listaId).document(itemId).delete().await()
    }
}