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

    /**
     * Busca as listas do usuário (Função completa)
     */
    suspend fun getMinhasListas(): List<ListaDeCompras> {
        val userId = getUserId() ?: return emptyList()
        try {
            val task = db.collection("listas")
                .whereEqualTo("userId", userId)
                .orderBy("nome")
                .get()
                .await()
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
        // TODO: Excluir itens da sub-coleção
    }

    // --- Funções de Edição (Adicionadas no Passo 6B) ---

    /**
     * Exclui uma imagem anterior do Storage (função auxiliar)
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
     * Busca uma única lista pelo seu ID
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
     * RF003: Atualiza uma lista existente no Firestore
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

        val dadosAtualizados = mapOf(
            "nome" to novoNome,
            "imageUrl" to novaUrlImagem
        )

        db.collection("listas").document(id).update(dadosAtualizados).await()
    }
}