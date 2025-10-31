package com.example.listadecomprasapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// O "Chef de Cozinha" da tela SuasListasActivity
class ListasViewModel : ViewModel() {

    // O Chef conhece o Gerente
    private val repository = ListasRepository

    // O "quadro de avisos" com a lista de pratos (listas)
    private val _listas = MutableLiveData<List<ListaDeCompras>>()
    val listas: LiveData<List<ListaDeCompras>> = _listas

    // O "quadro de avisos" de erros
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * O Garçom (Activity) chama esta função (ex: no onResume)
     * para pedir que o Chef carregue o cardápio.
     */
    fun carregarListas() {
        viewModelScope.launch {
            try {
                // 1. Pede a lista ao Gerente
                val minhasListas = repository.getMinhasListas()
                // 2. Coloca o resultado no quadro de avisos
                _listas.postValue(minhasListas)
            } catch (e: Exception) {
                // 3. Coloca o erro no quadro de avisos
                _error.postValue("Falha ao carregar listas: ${e.message}")
            }
        }
    }
}