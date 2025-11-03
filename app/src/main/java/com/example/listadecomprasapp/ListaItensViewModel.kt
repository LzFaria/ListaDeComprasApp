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

    fun carregarItens(listaId: String) {
        viewModelScope.launch {
            try {
                _itens.postValue(repository.getItensDaLista(listaId))
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    // Esta função estava dando erro
    fun atualizarItemComprado(listaId: String, item: ItemDaLista, comprado: Boolean) {
        viewModelScope.launch {
            try {
                item.comprado = comprado
                repository.atualizarItem(listaId, item) // Agora deve encontrar
                carregarItens(listaId)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    // Esta função estava dando erro
    fun excluirItem(listaId: String, itemId: String) {
        viewModelScope.launch {
            try {
                repository.excluirItem(listaId, itemId) // Agora deve encontrar
                carregarItens(listaId)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}