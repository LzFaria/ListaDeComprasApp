package com.example.listadecomprasapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ListaItensViewModel : ViewModel() {

    private val repository = ListasRepository

    private val _itens = MutableLiveData<List<ItemDaLista>>()
    val itens: LiveData<List<ItemDaLista>> = _itens

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * RF004 e RF005: Carrega os itens, agora passando o filtro.
     */
    fun carregarItens(listaId: String, filtro: String = "") {
        viewModelScope.launch {
            try {
                // Passa o ID da lista E o filtro para o Gerente
                _itens.postValue(repository.getItensDaLista(listaId, filtro))
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    /**
     * Atualiza o item e recarrega a lista (agora sem filtro)
     */
    fun atualizarItemComprado(listaId: String, item: ItemDaLista, comprado: Boolean) {
        viewModelScope.launch {
            try {
                item.comprado = comprado
                repository.atualizarItem(listaId, item)
                carregarItens(listaId) // Recarrega a lista completa
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    /**
     * Exclui o item e recarrega a lista (agora sem filtro)
     */
    fun excluirItem(listaId: String, itemId: String) {
        viewModelScope.launch {
            try {
                repository.excluirItem(listaId, itemId)
                carregarItens(listaId) // Recarrega
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}