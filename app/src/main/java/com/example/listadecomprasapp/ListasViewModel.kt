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
     * RF003 e RF005: Carrega as listas, agora passando o filtro.
     * Se o filtro estiver vazio, busca todas.
     */
    fun carregarListas(filtro: String = "") {
        viewModelScope.launch {
            try {
                // Passa o filtro para o Gerente
                val minhasListas = repository.getMinhasListas(filtro)
                _listas.postValue(minhasListas)
            } catch (e: Exception) {
                _error.postValue("Falha ao carregar listas: ${e.message}")
            }
        }
    }

    /**
     * Exclui a lista e recarrega a lista (agora sem filtro)
     */
    fun excluirLista(lista: ListaDeCompras) {
        viewModelScope.launch {
            try {
                repository.excluirLista(lista)
                carregarListas() // Recarrega a lista completa
            } catch (e: Exception) {
                _error.postValue("Falha ao excluir lista: ${e.message}")
            }
        }
    }
}