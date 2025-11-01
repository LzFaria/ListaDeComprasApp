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
     * Busca as listas do usuário (Sem mudanças)
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
            return emptyList()
        }
    }

    /**
     * Faz upload da imagem (Sem mudanças)
     */
    private suspend fun uploadImagemLista(uri: Uri): String {
        val userId = getUserId() ?: "unknown"
        val fileName = "listas/${userId}_${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(fileName)
        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }

    /**
     * Adiciona uma nova lista (Sem mudanças)
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
     * RF003: Exclui uma lista do Firestore e sua imagem do Storage
     */
    suspend fun excluirLista(lista: ListaDeCompras) {

        // --- AQUI ESTÁ A CORREÇÃO ---
        // 1. Criamos uma cópia local imutável (val) da URL.
        val urlDaImagem = lista.imageUrl

        // 2. Verificamos a CÓPIA, não a variável original.
        if (urlDaImagem != null) {
            try {
                // 3. Usamos a CÓPIA (que o Kotlin sabe que é segura).
                val storageRef = storage.getReferenceFromUrl(urlDaImagem)
                storageRef.delete().await()
            } catch (e: Exception) {
                // Se a imagem já foi deletada ou der erro, apenas continuamos.
                println("Erro ao deletar imagem do Storage: ${e.message}")
            }
        }

        // Exclui o documento da lista no Firestore
        db.collection("listas").document(lista.id).delete().await()

        // TODO: Excluir itens da sub-coleção
    }
}