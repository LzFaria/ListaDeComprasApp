package com.example.listadecomprasapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// O "Chef de Cozinha" da tela ListaItensActivity
class ListaItensViewModel : ViewModel() {

    private val repository = ListasRepository

    private val _itens = MutableLiveData<List<ItemDaLista>>()
    val itens: LiveData<List<ItemDaLista>> = _itens

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * Carrega os itens (com ou sem filtro de busca)
     */
    fun carregarItens(listaId: String, filtro: String = "") {
        viewModelScope.launch {
            try {
                _itens.postValue(repository.getItensDaLista(listaId, filtro))
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    /**
     * Atualiza o status "comprado" de um item
     */
    fun atualizarItemComprado(listaId: String, item: ItemDaLista, comprado: Boolean) {
        viewModelScope.launch {
            try {
                item.comprado = comprado
                repository.atualizarItem(listaId, item)
                carregarItens(listaId) // Recarrega a lista para mostrar a ordenação
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    /**
     * Exclui um item
     */
    fun excluirItem(listaId: String, itemId: String) {
        viewModelScope.launch {
            try {
                repository.excluirItem(listaId, itemId)
                carregarItens(listaId) // Recarrega a lista
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}