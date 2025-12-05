package com.example.listadecomprasapp.listas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listadecomprasapp.auth.AuthRepository
import com.example.listadecomprasapp.data.ListasRepository
import kotlinx.coroutines.launch

class ListasViewModel : ViewModel() {

    private val repository = ListasRepository
    private val authRepository = AuthRepository

    private val _listas = MutableLiveData<List<ListaDeCompras>>()
    val listas: LiveData<List<ListaDeCompras>> = _listas

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun carregarListas(filtro: String = "") {
        viewModelScope.launch {
            try {
                val minhasListas = repository.getMinhasListas(filtro)
                _listas.postValue(minhasListas)
            } catch (e: Exception) {
                _error.postValue("Falha ao carregar listas: ${e.message}")
            }
        }
    }

    fun excluirLista(lista: ListaDeCompras) {
        viewModelScope.launch {
            try {
                repository.excluirLista(lista)
                carregarListas() // Recarrega a lista
            } catch (e: Exception) {
                _error.postValue("Falha ao excluir lista: ${e.message}")
            }
        }
    }

    fun fazerLogout() {
        authRepository.fazerLogout()
    }
}