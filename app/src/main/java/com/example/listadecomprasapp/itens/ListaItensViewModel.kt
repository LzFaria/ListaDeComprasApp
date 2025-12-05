package com.example.listadecomprasapp.itens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listadecomprasapp.data.ListasRepository
import kotlinx.coroutines.launch

class ListaItensViewModel : ViewModel() {

    private val repository = ListasRepository

    private val _itens = MutableLiveData<List<ItemDaLista>>()
    val itens: LiveData<List<ItemDaLista>> = _itens

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _nomeDaLista = MutableLiveData<String>()
    val nomeDaLista: LiveData<String> = _nomeDaLista

    fun carregarItens(listaId: String, filtro: String = "") {
        viewModelScope.launch {
            try {
                _itens.postValue(repository.getItensDaLista(listaId, filtro))
            } catch (e: Exception) {
                _error.postValue("Falha ao carregar itens: ${e.message}")
            }
        }
    }

    fun carregarNomeDaLista(listaId: String) {
        viewModelScope.launch {
            try {
                val lista = repository.getListaPorId(listaId)
                if (lista != null) {
                    _nomeDaLista.postValue(lista.nome)
                } else {
                    _error.postValue("Lista não encontrada")
                }
            } catch (e: Exception) {
                _error.postValue("Falha ao carregar dados da lista: ${e.message}")
            }
        }
    }

    fun atualizarItemComprado(listaId: String, item: ItemDaLista, comprado: Boolean) {
        viewModelScope.launch {
            try {
                item.comprado = comprado
                repository.atualizarItem(listaId, item)
                carregarItens(listaId)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    fun excluirItem(listaId: String, itemId: String) {
        viewModelScope.launch {
            try {
                repository.excluirItem(listaId, itemId)
                carregarItens(listaId)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}