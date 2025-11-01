package com.example.listadecomprasapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ListasViewModel : ViewModel() {

    private val repository = ListasRepository

    private val _listas = MutableLiveData<List<ListaDeCompras>>()
    val listas: LiveData<List<ListaDeCompras>> = _listas

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * Carrega as listas (Sem mudanças)
     */
    fun carregarListas() {
        viewModelScope.launch {
            try {
                val minhasListas = repository.getMinhasListas()
                _listas.postValue(minhasListas)
            } catch (e: Exception) {
                _error.postValue("Falha ao carregar listas: ${e.message}")
            }
        }
    }

    // --- 1. NOVA FUNÇÃO ---
    /**
     * Pede ao "Gerente" para excluir a lista e depois
     * recarrega as listas restantes para atualizar a tela.
     */
    fun excluirLista(lista: ListaDeCompras) {
        viewModelScope.launch {
            try {
                // 1. Pede para excluir
                repository.excluirLista(lista)
                // 2. Pede para recarregar a lista (para a UI atualizar)
                carregarListas()
            } catch (e: Exception) {
                _error.postValue("Falha ao excluir lista: ${e.message}")
            }
        }
    }
}