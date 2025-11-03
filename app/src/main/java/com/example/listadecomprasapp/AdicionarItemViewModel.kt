package com.example.listadecomprasapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// O "Chef de Cozinha" da tela AdicionarItemActivity
class AdicionarItemViewModel : ViewModel() {

    private val repository = ListasRepository

    private val _concluido = MutableLiveData<Boolean>(false)
    val concluido: LiveData<Boolean> = _concluido

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _itemParaEditar = MutableLiveData<ItemDaLista?>()
    val itemParaEditar: LiveData<ItemDaLista?> = _itemParaEditar

    fun salvarItem(listaId: String, item: ItemDaLista) {
        _loading.postValue(true)
        viewModelScope.launch {
            try {
                repository.adicionarItem(listaId, item)
                _concluido.postValue(true)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    // Esta função estava dando "Unresolved reference"
    fun atualizarItem(listaId: String, item: ItemDaLista) {
        _loading.postValue(true)
        viewModelScope.launch {
            try {
                repository.atualizarItem(listaId, item) // Agora deve encontrar
                _concluido.postValue(true)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    // Esta função estava dando "Unresolved reference"
    fun carregarItem(listaId: String, itemId: String) {
        _loading.postValue(true)
        viewModelScope.launch {
            try {
                _itemParaEditar.postValue(repository.getItemPorId(listaId, itemId)) // Agora deve encontrar
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }
}