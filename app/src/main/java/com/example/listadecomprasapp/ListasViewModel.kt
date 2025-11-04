package com.example.listadecomprasapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ListasViewModel : ViewModel() {

    private val repository = ListasRepository
    // 1. ADICIONADO: O "Gerente" de Autenticação
    private val authRepository = AuthRepository

    private val _listas = MutableLiveData<List<ListaDeCompras>>()
    val listas: LiveData<List<ListaDeCompras>> = _listas

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * Carrega as listas (Função completa)
     */
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

    /**
     * Exclui a lista (Função completa)
     */
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

    // --- 2. A FUNÇÃO QUE FALTAVA (RF001) ---
    /**
     * Pede ao "Gerente" de Autenticação para fazer o logout
     */
    fun fazerLogout() {
        authRepository.fazerLogout()
    }
}