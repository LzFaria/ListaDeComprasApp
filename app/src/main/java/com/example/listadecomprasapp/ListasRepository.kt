package com.example.listadecomprasapp

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
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

    // --- Funções de LISTAS (Completas com Lógica de Busca) ---

    /**
     * RF003 e RF005: Busca as listas do usuário, aplicando um filtro de busca
     */
    suspend fun getMinhasListas(filtroDeBusca: String = ""): List<ListaDeCompras> {
        val userId = getUserId() ?: return emptyList()
        try {
            var query = db.collection("listas")
                .whereEqualTo("userId", userId)

            if (filtroDeBusca.isNotEmpty()) {
                query = query.whereGreaterThanOrEqualTo("nome", filtroDeBusca)
                    .whereLessThanOrEqualTo("nome", filtroDeBusca + '\uf8ff')
            }

            val task = query.orderBy("nome").get().await()
            return task.toObjects(ListaDeCompras::class.java)
        } catch (e: Exception) {
            println("Erro ao buscar listas: ${e.message}")
            return emptyList()
        }
    }

    /**
     * Faz upload da imagem (Função completa)
     */
    private suspend fun uploadImagemLista(uri: Uri): String {
        val userId = getUserId() ?: "unknown"
        val fileName = "listas/${userId}_${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(fileName)
        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }

    /**
     * Adiciona uma nova lista (Função completa)
     */
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

    /**
     * Exclui uma lista (Função completa)
     */
    suspend fun excluirLista(lista: ListaDeCompras) {
        excluirSubcolecaoItens(lista.id) // Exclui itens primeiro
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

    /**
     * Exclui subcoleção de itens (Função completa)
     */
    private suspend fun excluirSubcolecaoItens(listaId: String) {
        val snapshot = getCaminhoItens(listaId).get().await()
        val batch = db.batch()
        for (documento in snapshot.documents) {
            batch.delete(documento.reference)
        }
        batch.commit().await()
    }

    /**
     * Exclui uma imagem anterior (Função completa)
     */
    private suspend fun excluirImagemAnterior(imageUrl: String?) {
        if (imageUrl == null) return
        try {
            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.delete().await()
        } catch (e: Exception) {
            println("Erro ao deletar imagem anterior: ${e.message}")
        }
    }

    /**
     * Busca uma única lista pelo seu ID (Função completa)
     */
    suspend fun getListaPorId(id: String): ListaDeCompras? {
        try {
            val doc = db.collection("listas").document(id).get().await()
            return doc.toObject(ListaDeCompras::class.java)
        } catch (e: Exception) {
            println("Erro ao buscar lista por ID: ${e.message}")
            return null
        }
    }

    /**
     * Atualiza uma lista (Função completa)
     */
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
        val dadosAtualizados = mapOf("nome" to novoNome, "imageUrl" to novaUrlImagem)
        db.collection("listas").document(id).update(dadosAtualizados).await()
    }

    // --- Funções de ITENS (Completas com Lógica de Busca) ---

    private fun getCaminhoItens(listaId: String) =
        db.collection("listas").document(listaId).collection("itens")

    /**
     * RF004 e RF005: Busca todos os itens de uma lista, aplicando um filtro de busca
     */
    suspend fun getItensDaLista(listaId: String, filtroDeBusca: String = ""): List<ItemDaLista> {
        try {
            var query = getCaminhoItens(listaId)
                .orderBy("comprado")
                .orderBy("categoria")
                .orderBy("nome")

            if (filtroDeBusca.isNotEmpty()) {
                query = query.whereGreaterThanOrEqualTo("nome", filtroDeBusca)
                    .whereLessThanOrEqualTo("nome", filtroDeBusca + '\uf8ff')
            }

            val task = query.get().await()
            return task.toObjects(ItemDaLista::class.java)
        } catch (e: Exception) {
            println("Erro ao buscar itens: ${e.message}")
            return emptyList()
        }
    }

    /**
     * Adiciona um item (Função completa)
     */
    suspend fun adicionarItem(listaId: String, item: ItemDaLista) {
        getCaminhoItens(listaId).add(item).await()
    }

    /**
     * Busca um item por ID (Função completa)
     */
    suspend fun getItemPorId(listaId: String, itemId: String): ItemDaLista? {
        try {
            val doc = getCaminhoItens(listaId).document(itemId).get().await()
            return doc.toObject(ItemDaLista::class.java)
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Atualiza um item (Função completa)
     */
    suspend fun atualizarItem(listaId: String, item: ItemDaLista) {
        getCaminhoItens(listaId).document(item.id).set(item).await()
    }

    /**
     * Exclui um item (Função completa)
     */
    suspend fun excluirItem(listaId: String, itemId: String) {
        getCaminhoItens(listaId).document(itemId).delete().await()
    }
}