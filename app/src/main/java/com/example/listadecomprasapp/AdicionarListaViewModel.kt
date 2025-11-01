package com.example.listadecomprasapp

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AdicionarListaViewModel : ViewModel() {

    private val repository = ListasRepository

    // --- Quadro de avisos (sem mudanças) ---
    private val _concluido = MutableLiveData<Boolean>(false)
    val concluido: LiveData<Boolean> = _concluido
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    // --- 1. NOVO QUADRO DE AVISOS ---
    // Para entregar ao Garçom (Activity) a lista que ele precisa editar
    private val _listaParaEditar = MutableLiveData<ListaDeCompras?>()
    val listaParaEditar: LiveData<ListaDeCompras?> = _listaParaEditar

    /**
     * Salva uma NOVA lista (Sem mudanças)
     */
    fun salvarLista(nome: String, uriLocal: Uri?) {
        _loading.postValue(true)
        viewModelScope.launch {
            try {
                repository.adicionarLista(nome, uriLocal)
                _concluido.postValue(true)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    // --- 2. NOVAS FUNÇÕES ---

    /**
     * Busca os dados da lista que o usuário quer editar
     */
    fun carregarLista(id: String) {
        _loading.postValue(true)
        viewModelScope.launch {
            try {
                // Pede ao Gerente para buscar a lista por ID
                val lista = repository.getListaPorId(id)
                // Coloca no quadro de avisos
                _listaParaEditar.postValue(lista)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    /**
     * Salva as MUDANÇAS de uma lista existente
     */
    fun atualizarLista(
        id: String,
        novoNome: String,
        novaUriLocal: Uri?,
        urlImagemAntiga: String?
    ) {
        _loading.postValue(true)
        viewModelScope.launch {
            try {
                // Pede ao Gerente para atualizar
                repository.atualizarLista(id, novoNome, novaUriLocal, urlImagemAntiga)
                // Avisa que terminou
                _concluido.postValue(true)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }
}