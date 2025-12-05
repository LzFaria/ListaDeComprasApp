package com.example.listadecomprasapp.listas

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listadecomprasapp.data.ListasRepository
import kotlinx.coroutines.launch

class AdicionarListaViewModel : ViewModel() {

    private val repository = ListasRepository

    private val _concluido = MutableLiveData<Boolean>(false)
    val concluido: LiveData<Boolean> = _concluido
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading


    private val _listaParaEditar = MutableLiveData<ListaDeCompras?>()
    val listaParaEditar: LiveData<ListaDeCompras?> = _listaParaEditar


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

    fun carregarLista(id: String) {
        _loading.postValue(true)
        viewModelScope.launch {
            try {
                val lista = repository.getListaPorId(id)
                _listaParaEditar.postValue(lista)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun atualizarLista(
        id: String,
        novoNome: String,
        novaUriLocal: Uri?,
        urlImagemAntiga: String?
    ) {
        _loading.postValue(true)
        viewModelScope.launch {
            try {
                repository.atualizarLista(id, novoNome, novaUriLocal, urlImagemAntiga)
                _concluido.postValue(true)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _loading.postValue(false)
            }
        }
    }
}