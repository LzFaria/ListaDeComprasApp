package com.example.listadecomprasapp

import android.net.Uri // <-- NOVO IMPORT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage // <-- NOVO IMPORT
import kotlinx.coroutines.tasks.await
import java.util.UUID // <-- NOVO IMPORT

object ListasRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val storage = Firebase.storage // 1. O "Estoque" de Imagens

    // Retorna o UID do usuário logado ou null
    private fun getUserId(): String? = auth.currentUser?.uid

    /**
     * Busca as listas do usuário (função que já tínhamos)
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
     * RF003: Faz upload de uma imagem (Uri local) para o Firebase Storage
     * e retorna a URL (String) de download.
     */
    private suspend fun uploadImagemLista(uri: Uri): String {
        // Cria um nome de arquivo único (ex: 'listas/uid_aleatorio.jpg')
        val userId = getUserId() ?: "unknown"
        val fileName = "listas/${userId}_${UUID.randomUUID()}.jpg"

        // 1. Pega a referência do arquivo no Storage
        val storageRef = storage.reference.child(fileName)
        // 2. Faz o upload do arquivo
        storageRef.putFile(uri).await()
        // 3. Pega a URL de download pública do arquivo
        return storageRef.downloadUrl.await().toString()
    }

    /**
     * RF003: Adiciona uma nova lista no Firestore
     */
    suspend fun adicionarLista(nome: String, uriLocal: Uri?) {
        val userId = getUserId() ?: return // Precisa estar logado

        var downloadUrl: String? = null
        if (uriLocal != null) {
            // Se o usuário escolheu uma imagem, faz o upload
            downloadUrl = uploadImagemLista(uriLocal)
        }

        // Cria o "molde" para salvar no Firestore
        val novaLista = ListaDeCompras(
            // O Firestore vai gerar o 'id'
            nome = nome,
            userId = userId,
            imageUrl = downloadUrl
        )

        // Salva o objeto 'novaLista' na coleção 'listas'
        db.collection("listas").add(novaLista).await()
    }

    // TODO: Funções de editar e excluir
}